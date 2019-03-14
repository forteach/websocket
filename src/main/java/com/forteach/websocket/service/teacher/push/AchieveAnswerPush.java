package com.forteach.websocket.service.teacher.push;

import com.forteach.websocket.domain.*;
import com.forteach.websocket.service.StudentsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import static com.forteach.websocket.common.Dic.ASK_CIRCLE_ANSWER_DID;

/**
 * @Description:学生回答推送给老师
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/20  14:29
 */
@Slf4j
@Component
public class AchieveAnswerPush {

    @Resource
    private TeacherInteractImpl TeacherInteract;

    @Resource
    private StudentsService studentsService;


    /**
     * 将课堂加入的学生回答数据，推送给老师
     * circleId 课堂编号
     *teachseId 接受推送的教师
     * @return
     */
    public ToTeacherPush getAchieveAnswer(final String circleId) {
        //获得需要课堂的教师ID
        final String teachseId=TeacherInteract.getRoomTeacherId(circleId);
        //创建回答信息
        return ToTeacherPush.builder()
                .uid(teachseId)
                //学生回答信息(BigQuestion)
                .achieveAnswer(achieveAnswer(circleId))
                .build();

    }

    /**
     * 主动推送给教师 当前问题的回答情况
     *
     * @return
     */
    public AchieveAnswer achieveAnswer(final String circleId) {
        //获得回答cut随机值
        String uRandom = "";
        //获得题目ID
        final String questionId =TeacherInteract.getNowQuestionId(circleId);
        //获得学生的回答信息
        List<Students> students = peopleAnswer(circleId, questionId, QuestionType.TiWen);
        return buildAchieveAnswer(students);

    }

    /**
     * 构建学生回答的推送信息
     *
     * @param students
     * @return
     */
    private AchieveAnswer buildAchieveAnswer(final List<Students> students) {
        return new AchieveAnswer(students);
    }

    /**
     * 获取回答的学生情况
     *
     * @param uCircle
     * @param questionId
     * @return
     */
    private List<Students> peopleAnswer(final String uCircle, final String questionId, final QuestionType type) {
        return TeacherInteract.getAnswerStudent(uCircle,questionId,type.name()).stream().map(stuid -> {
            //查询redis 筛选是否回答情况
            Students student = studentsService.findStudentsBrief(stuid);
            //学生回答的答案
            String askAnswerInfo=TeacherInteract.getQuestAnswer(uCircle,questionId,type.name(),stuid);
            //获得学生的批改结果
            String piGaiResult=TeacherInteract.piGaiResult(uCircle,questionId,type.name(),stuid);
            //创建学生回答推送对象
            return new CircleAnswer(uCircle,questionId,student, ASK_CIRCLE_ANSWER_DID, askAnswerInfo,piGaiResult);

        }).collect(Collectors.toList());
    }

//    /**
//     * 查找学生的回答信息
//     *
//     * @param circleId
//     * @param examineeId
//     * @param questionId
//     * @return
//     */
//    private Object findAskAnswer(final String circleId, final String examineeId, final String questionId, final QuestionType type) {
//
//        if (type.equals(QuestionType.TiWen)) {
//            Query query = Query.query(
//                    Criteria.where("circleId").is(circleId)
//                            .and("questionId").is(questionId)
//                            .and("examineeId").is(examineeId));
//
//            return mongoTemplate.findOne(query, AskAnswer.class);
//        }
//
//        Query query = Query.query(
//                Criteria.where("circleId").is(circleId)
//                        .and("examineeId").is(examineeId)
//                        .and("libraryType").is(type.name()));
//        ActivityAskAnswer activityAskAnswer = mongoTemplate.findOne(query, ActivityAskAnswer.class);
//        return activityAskAnswerResults(activityAskAnswer);
//    }
//
//    private Object activityAskAnswerResults(ActivityAskAnswer activityAskAnswer) {
//        if (activityAskAnswer == null) {
//            return null;
//        }
//        return activityAskAnswer.getAnswList();
//    }

}
