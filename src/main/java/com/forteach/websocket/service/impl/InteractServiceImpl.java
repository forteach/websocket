package com.forteach.websocket.service.impl;

import com.forteach.websocket.domain.ToPush;
import com.forteach.websocket.service.InteractService;
import com.forteach.websocket.service.RedisInteract;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.forteach.websocket.common.Dic.SUBSCRIBE_USER_STUDENT;
import static com.forteach.websocket.common.KeyStorage.INTERACTION_UID_SET_PREFIX;
import static com.forteach.websocket.service.WsService.SESSION_MAP;

/**
 * @Description: 互动交互数据获取
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/28  9:38
 */
@Slf4j
@Service
public class InteractServiceImpl implements InteractService {

    @Resource
    private RedisInteract interact;

    @Resource
    private StudentToPush studentToPush;

    @Resource
    private TeachersToPush teachersToPush;

    /**
     * 获取课堂交互信息
     *
     * @return
     */
    @Override
    public List<ToPush> obtain() {
        // 从redis取出加入的学生信息
        Set<String> uid = interact.getSets(INTERACTION_UID_SET_PREFIX);
        if (uid != null && uid.size() > 0) {
            //构建推送对象信息集合
            return uid.stream()
                    .filter(id -> null != SESSION_MAP.get(id))
                    .filter(id -> SESSION_MAP.get(id).isOpen())
                    .map(this::buildToPush)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * 构建需要推送的信息
     *
     * @param uid
     * @return
     */
    private ToPush buildToPush(String uid) {
        // 获取要推送的用户身份信息 teacher student
        String uType = interact.uidType(uid);
        if (SUBSCRIBE_USER_STUDENT.equals(uType)) {
            //是学生推送学生信息
            return ToPush.builder()
                    .uid(uid)
                    //提问问题
                    .askQuestion(studentToPush.achieveQuestion(uid))
                    //学生习题任务
                    .askSurvey(studentToPush.achieveSurvey(uid))
                    //头脑风暴
                    .askBrainstorm(studentToPush.achieveBrainstorm(uid))
                    //学生习题任务
                    .askTask(studentToPush.achieveTask(uid))
                    //习题册(练习册)
                    .askBook(studentToPush.achieveBook(uid))
                    .build();
        } else {
            //不是学生推送老师信息
            return ToPush.builder()
                    .uid(uid)
                    //学生回答信息(BigQuestion)
                    .achieveAnswer(teachersToPush.achieveAnswer(uid))
                    //学生举手信息
                    .achieveRaise(teachersToPush.achieveRaise(uid))
                    //学生加入课堂信息
                    .achieveJoin(teachersToPush.achieveInteractiveStudents(uid))
                    //实时学生问卷答案
                    .achieveSurveyAnswer(teachersToPush.achieveSurveyAnswer(uid))
                    //头脑风暴答案
                    .achieveBrainstormAnswer(teachersToPush.achieveBrainstormAnswer(uid))
                    //任务答案
                    .achieveTaskAnswer(teachersToPush.achieveTaskAnswer(uid))
                    //习题答案
                    .achieveBookAnswer(teachersToPush.achieveBookAnswer(uid))
                    .build();
        }
    }
}
