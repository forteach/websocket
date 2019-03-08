package com.forteach.websocket.service.impl;

import com.forteach.websocket.common.*;
import com.forteach.websocket.domain.ToStudentPush;
import com.forteach.websocket.domain.ToTeacherPush;
import com.forteach.websocket.service.InteractService;
import com.forteach.websocket.service.RedisInteract;
import com.forteach.websocket.service.student.push.TiWenPush;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import static com.forteach.websocket.common.Dic.SUBSCRIBE_USER_STUDENT;
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
    private TiWenPush tiWenPush;

    @Resource
    private TeachersToPush teachersToPush;

    /**
     * 获取课堂交互信息
     *
     * @return
     */
    @Override
    public List<ToTeacherPush> obtainTeacher(String circleId) {
        // 从redis取出加入的学生信息
        Set<String> uid = interact.getSets(KeyStorage.INTERACTION_UID_SET_PREFIX);
        if (uid != null && uid.size() > 0) {
            //构建推送对象信息集合
            return uid.stream()
                    .filter(id -> null != SESSION_MAP.get(id))
                    .filter(id -> SESSION_MAP.get(id).isOpen())
                    .map(this::buildTeacherToPush)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

//    /**
//     * 获取单个课堂推送给学生交互信息
//     * @param circleId 课堂编号
//     * @param teachId 教师ID
//     * @return
//     */
//    @Override
//    public List<ToStudentPush> obtainStudent(String circleId,String teachId) {
//        // 从redis取出加入的学生信息
//       // Set<String> uid = interact.getSets(INTERACTION_UID_SET_PREFIX);
//        //获得课堂ID，正在上课的学生
////        final Set<String> uid = interact.getSets(ClassRoomKey.getInteractiveIdQra(circleId))
//        return interact.getSets(ClassRoomKey.getInteractiveIdQra(circleId))
//                .stream()
//                .filter(teachId::equals)//过滤掉班级教师ID
//                .filter(id -> null != SESSION_MAP.get(id))
//                .filter(id -> SESSION_MAP.get(id).isOpen())
//                .map(this::buildStudentToPush)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//
////                .collect(Collectors.toSet());
//
////        if (uid != null && uid.size() > 0) {
////            //构建推送对象信息集合
////            return uid.stream()
////                    .filter(id -> null != SESSION_MAP.get(id))
////                    .filter(id -> SESSION_MAP.get(id).isOpen())
////                    .map(this::buildStudentToPush)
////                    .filter(Objects::nonNull)
////                    .collect(Collectors.toList());
////        }
////        return new ArrayList<>();
//    }


    //根据课堂编号，获得需要推送给学生的提问信息
    /**
     *
     * @param circleId
     * @return
     */
    public List<ToStudentPush> tiWenStudent(String circleId){

        //获得提问方式的题目编号
       final String questId=interact.getNowQuestId(circleId);
       //获得当前题目选中的学生
        final String stus= interact.getQuestStu(circleId,questId);

        //暂时设定，需要从redis里面去除该值
        String interactive=Dic.ASK_INTERACTIVE_SELECT;  //交互方式  选人、举手、抢答
        //暂时设定，需要从redis里面去除该值
        String category=Dic.CATEGORY_PEOPLE;  //小组 个人

       //根据所选的学生，对比Session数据是否在线，并获得学生推送详情
        return Arrays.asList(stus.split(",")).stream()
                .filter(id -> null != SESSION_MAP.get(id))
                .filter(id -> SESSION_MAP.get(id).isOpen())

                .map(uid->buildStudentToPush(uid,questId, interactive, category,QuestionType.TiWen))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

//        if (uid != null && uid.size() > 0) {
//            //构建推送对象信息集合
//            return uid.stream()
//                    .filter(id -> null != SESSION_MAP.get(id))
//                    .filter(id -> SESSION_MAP.get(id).isOpen())
//                    .map(this::buildStudentToPush)
//                    .filter(Objects::nonNull)
//                    .collect(Collectors.toList());
//        }
//        return new ArrayList<>();
    }


    /**
     * 构建需要推送的信息(教师端)
     *
     * @param uid
     * @return
     */
    private ToTeacherPush buildTeacherToPush(String uid) {
        // 获取要推送的用户身份信息 teacher student
        String uType = interact.uidType(uid);
        if (!SUBSCRIBE_USER_STUDENT.equals(uType)) {
            return ToTeacherPush.builder()
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
        return null;
    }

    /**
     * 推送学生数据对象构造
     ** @param uid 学生编号
     * @param questid 题目编号
     * @param interactive  交互方式  选人、举手、抢答
     * @param category  小组 个人
     * @param type  参与的活动   提问 练习  风暴等
     * @return
     */
    private ToStudentPush buildStudentToPush(String uid,String questid,String interactive,String category,QuestionType type) {

           switch (type){
               case TiWen:
               //是学生推送学生信息
               return ToStudentPush.builder()
                       .uid(uid)
                       //提问问题
                       .askQuestion(tiWenPush.achieveQuestion(questid,interactive,category))
                       .build();
               case WenJuan:
                   return ToStudentPush.builder()
                           .uid(uid)
                           //学生习题任务
                           .askSurvey(studentToPush.achieveSurvey(uid))
                           .build();
               case FengBao:
                   return ToStudentPush.builder()
                           .uid(uid)
                           //头脑风暴
                           .askBrainstorm(studentToPush.achieveBrainstorm(uid))
                           .build();
               case RenWu:
                   return ToStudentPush.builder()
                           .uid(uid)
                           .askTask(studentToPush.achieveTask(uid))
                           .build();
               case LianXi:
                   return ToStudentPush.builder()
                           .uid(uid)
                           //习题册(练习册)
                           .askBook(studentToPush.achieveBook(uid))
                           .build();
           }
           return null;
    }
}
