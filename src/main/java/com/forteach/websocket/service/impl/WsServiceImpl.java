package com.forteach.websocket.service.impl;

import com.alibaba.fastjson.JSON;
import com.forteach.websocket.common.BigQueKey;
import com.forteach.websocket.common.ClassRoomKey;
import com.forteach.websocket.domain.ToStudentPush;
import com.forteach.websocket.domain.ToTeacherPush;
import com.forteach.websocket.service.WsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.websocket.Session;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static com.forteach.websocket.common.KeyStorage.INTERACTION_UID_SET_PREFIX;
import static com.forteach.websocket.common.KeyStorage.actionPropertyKey;

/**
 * @Description: 处理用户链接相应的班级 进行推送信息链接
 * @author: zjw
 * @version: V1.0
 * @date: 2018/12/27  9:38
 */
@Slf4j
@Service
public class WsServiceImpl implements WsService {

    @Resource
    private HashOperations<String, String, String> hashOperations;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //old
    @Override
    public boolean subscript(String circle, String uid, String type, String random, Session sessions) {
        try {
            //存入 用户uid set
            stringRedisTemplate.opsForSet().add(INTERACTION_UID_SET_PREFIX, uid);

            //存入 用户互动的hash属性
            String interactKey = actionPropertyKey(uid);
            hashOperations.put(interactKey, "circle", circle);
            hashOperations.put(interactKey, "type", type);
            hashOperations.put(interactKey, "random", random);

            //用户hash属性,设置有效期
            stringRedisTemplate.expire(interactKey, 2L, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("subscript error {}", e.getMessage());
        }
        return true;
    }

    /**
     * new
     * @param circle  课堂ID
     * @param uid   加入的用户
     * @param random  每次连接的随机数
     * @return
     */
    @Override
    public boolean subscript(String circle, String uid,String random){

        //保存课堂用户连接的随机数
        if(stringRedisTemplate.hasKey(ClassRoomKey.getOpenClassRandom(circle,uid))){
            //获得当前用户的随机数
           String radon= stringRedisTemplate.opsForValue().get(ClassRoomKey.getOpenClassRandom(circle,uid));
           //两次随机数不相等
           if(!radon.equals(random) )
           {
                //随机数已经改变
               stringRedisTemplate.opsForValue().set(ClassRoomKey.getOpenClassRandomTag(circle,uid, BigQueKey.CLASSROOM_CLEAR_TAG_JION),ClassRoomKey.OPEN_CLASSROOM_Random_TAG_YES, Duration.ofSeconds(60*60*2));
               stringRedisTemplate.opsForValue().set(ClassRoomKey.getOpenClassRandomTag(circle,uid,BigQueKey.CLASSROOM_CLEAR_TAG_ANSWER),ClassRoomKey.OPEN_CLASSROOM_Random_TAG_YES, Duration.ofSeconds(60*60*2));
               stringRedisTemplate.opsForValue().set(ClassRoomKey.getOpenClassRandomTag(circle,uid,BigQueKey.CLASSROOM_CLEAR_TAG_RAISE),ClassRoomKey.OPEN_CLASSROOM_Random_TAG_YES, Duration.ofSeconds(60*60*2));
           }
        }else{

              stringRedisTemplate.opsForValue().set(ClassRoomKey.getOpenClassRandomTag(circle,uid,BigQueKey.CLASSROOM_CLEAR_TAG_JION),ClassRoomKey.OPEN_CLASSROOM_Random_TAG_NO, Duration.ofSeconds(60*60*2));
              stringRedisTemplate.opsForValue().set(ClassRoomKey.getOpenClassRandomTag(circle,uid,BigQueKey.CLASSROOM_CLEAR_TAG_ANSWER),ClassRoomKey.OPEN_CLASSROOM_Random_TAG_NO, Duration.ofSeconds(60*60*2));
              stringRedisTemplate.opsForValue().set(ClassRoomKey.getOpenClassRandomTag(circle,uid,BigQueKey.CLASSROOM_CLEAR_TAG_RAISE),ClassRoomKey.OPEN_CLASSROOM_Random_TAG_NO, Duration.ofSeconds(60*60*2));
        }
        //设置缓存随机数
        stringRedisTemplate.opsForValue().set(ClassRoomKey.getOpenClassRandom(circle,uid),random, Duration.ofSeconds(60*60*2));

        return true;
    }

    /**
     * 通过uid解除订阅
     *
     * @param uid
     * @return
     */
    @Override
    public boolean unSubscript(String uid) {
        try {
//            stringRedisTemplate.opsForSet().remove(INTERACTION_UID_SET_PREFIX, uid);
//            stringRedisTemplate.delete(actionPropertyKey(uid));
        } catch (Exception e) {
            log.error("unSubscript error {}", e.getMessage());
        }
        return true;
    }

    @Override
    public Session getSession(String uid) {
        return SESSION_MAP.get(uid);
    }

    /**
     * remove 后 资源释放
     *
     * @param uid 指定的用户id
     * @return
     */
    @Override
    public boolean removeSession(String uid) {
        try {
            Session s = SESSION_MAP.remove(uid);
            s = null;
        } catch (Exception e) {
            log.error(">>>移除用户session时失败，待移除的用户id: {}", uid);
        }
        return true;
    }

    @Override
    public boolean registerSession(String uid, Session session) {
        try {
            SESSION_MAP.put(uid, session);
        } catch (Exception e) {
            log.error(">>> 注册用户session 时失败，注册用户:{}, session:{}", uid, session);
            return false;
        }
        return true;
    }

    /**
     * 循环处理推送信息
     * @param list
     */
    @Override
    public void processStudent(List<ToStudentPush> list) {
        list.forEach(toPush -> {
            Session session = SESSION_MAP.get(toPush.getUid());
            sendStudentMessage(toPush, session);
        });
    }

    /**
     * 循环处理推送信息
     * @param list
     */
    @Override
    public void processTeacher(List<ToTeacherPush> list) {
        list.forEach(toPush -> {
            Session session = SESSION_MAP.get(toPush.getUid());
            sendTeacherMessage(toPush, session);
        });
    }

    /**
     * 循环处理推送信息
     * @param obj
     */
    @Override
    public void processTeacher(ToTeacherPush obj) {
            Session session = SESSION_MAP.get(obj.getUid());
            sendTeacherMessage(obj, session);
    }

    //推动给教师信息
    private void sendTeacherMessage(ToTeacherPush toPush, Session session) {
        //必须session 存在并且是开启状态才能推送
        boolean effective = effective(session);
        try {
            //学生举手信息
            if (effective && toPush.getAchieveRaise() != null) {
                session.getBasicRemote().sendText(JSON.toJSONString(toPush.getAchieveRaise()));
                log.info("推送给老师 举手 uid {}", toPush.getUid());
            }
            //实时学生问卷答案
            if (effective && toPush.getAchieveAnswer() != null) {
                session.getBasicRemote().sendText(JSON.toJSONString(toPush.getAchieveAnswer()));
                log.info("推送给老师 学生回答情况 uid {}", toPush.getUid());
            }
            //头学生加入信息
            if (effective && toPush.getAchieveJoin() != null) {
                session.getBasicRemote().sendText(JSON.toJSONString(toPush.getAchieveJoin()));
                log.info("推送给老师 学生加入情况 uid {}", toPush.getUid());
            }
            //实时学生问卷答案
            if (effective && toPush.getAchieveSurveyAnswer() != null) {
                session.getBasicRemote().sendText(JSON.toJSONString(toPush.getAchieveSurveyAnswer()));
                log.info("推送给老师 学生问卷答案 uid {}", toPush.getUid());
            }
            //头脑风暴答案
            if (effective && toPush.getAchieveBrainstormAnswer() != null) {
                session.getBasicRemote().sendText(JSON.toJSONString(toPush.getAchieveBrainstormAnswer()));
                log.info("推送给老师 学生头脑风暴答案 uid {}", toPush.getUid());
            }
            //任务答案
            if (effective && toPush.getAchieveTaskAnswer() != null) {
                session.getBasicRemote().sendText(JSON.toJSONString(toPush.getAchieveTaskAnswer()));
                log.info("推送给老师 学生任务答案 uid {}", toPush.getUid());
            }
            //习题答案
            if (effective && toPush.getAchieveBookAnswer() != null) {
                session.getBasicRemote().sendText(JSON.toJSONString(toPush.getAchieveBookAnswer()));
                log.info("推送给老师 习题答案 uid {}", toPush.getUid());
            }
        } catch (IOException e) {
            log.error(">>> sendTeacherMessage 时失败，{}", e.getMessage());
        }
    }

    //推动给学生信息
    private void sendStudentMessage(ToStudentPush toPush, Session session) {
        //必须session 存在并且是开启状态才能推送
        boolean effective = effective(session);
        try {
            //提问问题(BigQuestion)
            if (effective && toPush.getAskQuestion() != null) {
                session.getBasicRemote().sendText(JSON.toJSONString(toPush.getAskQuestion()));
                log.info("推送给学生 问题 uid {}", toPush.getUid());
            }
            //学生习题任务
            if (effective && toPush.getAskTask() != null) {
                session.getBasicRemote().sendText(JSON.toJSONString(toPush.getAskTask()));
                log.info("推送给学生 任务问题 uid {}", toPush.getUid());

            }
            //习题册(练习册)
            if (effective && toPush.getAskBook() != null) {
                session.getBasicRemote().sendText(JSON.toJSONString(toPush.getAskBook()));
                log.info("推送给学生 练习册问题 uid {}", toPush.getUid());

            }
            //头脑风暴
            if (effective && toPush.getAskBrainstorm() != null) {
                session.getBasicRemote().sendText(JSON.toJSONString(toPush.getAskBrainstorm()));
                log.info("推送给学生 头脑风暴问题 uid {}", toPush.getUid());

            }
            //学生获取问卷问题
            if (effective && toPush.getAskSurvey() != null) {
                session.getBasicRemote().sendText(JSON.toJSONString(toPush.getAskSurvey()));
                log.info("推送给学生 问卷问题 uid {}", toPush.getUid());
            }
        } catch (IOException e) {
            log.error(">>> sendStudentMessage 时失败，{}", e.getMessage());
        }
    }


    private boolean effective(Session session) {
        return null != session && session.isOpen();
    }

}
