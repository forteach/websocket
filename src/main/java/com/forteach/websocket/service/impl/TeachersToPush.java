package com.forteach.websocket.service.impl;

import com.forteach.websocket.domain.*;
import com.forteach.websocket.service.RedisInteract;
import com.forteach.websocket.service.StudentsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import static com.forteach.websocket.common.Dic.*;
import static com.forteach.websocket.common.KeyStorage.*;

/**
 * @Description: 推送教师相关信息
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/20  14:30
 */
@Slf4j
@Component
public class TeachersToPush {

    @Resource
    private RedisInteract interact;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private StudentsService studentsService;

    /**
     * 主动推送给教师 当前问题的回答情况
     *
     * @return
     */
    public AchieveAnswer achieveAnswer(String uid) {

        String uCircle = interact.uidCircle(uid);
        String uRandom = interact.uidRandom(uid);
        if (uCircle == null || uRandom == null) {
            return null;
        }
        String askKey = CLASSROOM_ASK_QUESTIONS_ID.concat(QuestionType.BigQuestion.name()).concat(uCircle);
        String questionId = interact.askQuestionId(askKey);
        if (questionId == null) {
            return null;
        }
        if (!interact.untitled(askKey)) {
            return null;
        }

        if (interact.answerDistinct(getAnswDistinctKey(uCircle, uRandom), examineeIsReplyKey(QuestionType.BigQuestion, uCircle), askKey)) {
            List<Students> students = peopleAnswer(uCircle, questionId, askKey, QuestionType.BigQuestion);
            return buildAchieveAnswer(students);
        } else {
            return null;
        }
    }

    public AchieveSurveyAnswer achieveSurveyAnswer(String uid) {
        String uCircle = interact.uidCircle(uid);
        String uRandom = interact.uidRandom(uid);
        if (uCircle == null || uRandom == null) {
            return null;
        }
        String askKey = CLASSROOM_ASK_QUESTIONS_ID.concat(QuestionType.SurveyQuestion.name()).concat(uCircle);
        String questionId = interact.askQuestionId(askKey);
        if (questionId == null) {
            return null;
        }
        if (!interact.untitled(askKey)) {
            return null;
        }
        if (interact.answerDistinct(getAnswDistinctKey(uCircle, uRandom), examineeIsReplyKey(QuestionType.SurveyQuestion, uCircle), askKey)) {
            //获取学生回答情况信息列表
            List<Students> students = peopleAnswer(uCircle, questionId, askKey, QuestionType.SurveyQuestion);
            return buildAchieveSurveyAnswer(students);
        } else {
            return null;
        }
    }

    public AchieveBrainstormAnswer achieveBrainstormAnswer(String uid) {
        String uCircle = interact.uidCircle(uid);
        String uRandom = interact.uidRandom(uid);
        if (uCircle == null || uRandom == null) {
            return null;
        }
        String askKey = CLASSROOM_ASK_QUESTIONS_ID.concat(QuestionType.BrainstormQuestion.name()).concat(uCircle);
        String questionId = interact.askQuestionId(askKey);
        if (questionId == null) {
            return null;
        }
        if (!interact.untitled(askKey)) {
            return null;
        }
        if (interact.answerDistinct(getAnswDistinctKey(uCircle, uRandom), examineeIsReplyKey(QuestionType.BrainstormQuestion, uCircle), askKey)) {
            List<Students> students = peopleAnswer(uCircle, questionId, askKey, QuestionType.BrainstormQuestion);
            return buildAchieveBrainstormAnswer(students);
        } else {
            return null;
        }
    }

    public AchieveTaskAnswer achieveTaskAnswer(String uid) {
        String uCircle = interact.uidCircle(uid);
        String uRandom = interact.uidRandom(uid);
        if (uCircle == null || uRandom == null) {
            return null;
        }
        String askKey = CLASSROOM_ASK_QUESTIONS_ID.concat(QuestionType.TaskQuestion.name()).concat(uCircle);
        String questionId = interact.askQuestionId(askKey);
        if (questionId == null) {
            return null;
        }
        if (!interact.untitled(askKey)) {
            return null;
        }
        if (interact.answerDistinct(getAnswDistinctKey(uCircle, uRandom), examineeIsReplyKey(QuestionType.TaskQuestion, uCircle), askKey)) {
            List<Students> students = peopleAnswer(uCircle, questionId, askKey, QuestionType.TaskQuestion);
            return buildAchieveTaskAnswer(students);
        } else {
            return null;
        }
    }

    public AchieveBookAnswer achieveBookAnswer(String uid) {
        String uCircle = interact.uidCircle(uid);
        String uRandom = interact.uidRandom(uid);
        if (uCircle == null || uRandom == null) {
            return null;
        }
        String askKey = CLASSROOM_ASK_QUESTIONS_ID.concat(QuestionType.ExerciseBook.name()).concat(uCircle);
        String questionId = interact.askQuestionId(askKey);
        if (questionId == null) {
            return null;
        }
        if (!interact.untitled(askKey)) {
            return null;
        }
        if (interact.answerDistinct(getAnswDistinctKey(uCircle, uRandom), examineeIsReplyKey(QuestionType.ExerciseBook, uCircle), askKey)) {
            List<Students> students = peopleAnswer(uCircle, questionId, askKey, QuestionType.ExerciseBook);
            return buildAchieveBookAnswer(students);
        } else {
            return null;
        }
    }

    /**
     * 获取回答的学生情况
     *
     * @param uCircle
     * @param questionId
     * @param askKey
     * @return
     */
    private List<Students> peopleAnswer(String uCircle, String questionId, String askKey, final QuestionType type) {
        return interact.getAnswerStudent(askKey).stream().map(id -> {
            //查询redis 筛选是否回答情况
            boolean flag = interact.isMember(examineeIsReplyKey(type, uCircle), id);
            Object answ = findAskAnswer(uCircle, id, questionId, type);
            Students student = studentsService.findStudentsBrief(id);

            if (flag) {
                log.debug("peopleAnswer 获得了数据 推送");
                return new CircleAnswer(student, ASK_CIRCLE_ANSWER_DID, answ);
            } else {
                log.debug("peopleAnswer 没有获得数据 进行重试");

                for (int i = 0; i < 20; i++) {

                    try {
                        Thread.sleep(1);
                        flag = interact.isMember(examineeIsReplyKey(type, uCircle), id);
                        answ = findAskAnswer(uCircle, id, questionId, type);
                        if (flag) {
                            log.debug("通过休眠获得到最新数据 ,共休眠次数 {} 每次 {} millis", i + 1, 1);
                            return new CircleAnswer(student, ASK_CIRCLE_ANSWER_DID, answ);
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException("线程休眠异常");
                    }
                }
                log.debug("peopleAnswer 没有获得数据 重试结束");
                return new CircleAnswer(student, ASK_CIRCLE_ANSWER_ALREADY, new AskAnswer());
            }

        }).collect(Collectors.toList());
    }


    /**
     * 构建举手的推送信息
     *
     * @param students
     * @return
     */
    private AchieveRaise buildAchieveRaise(List<Students> students) {
        return new AchieveRaise(students);
    }

    /**
     * 构建加入学生的推送信息
     *
     * @param students
     * @return
     */
    private AchieveJoin buildAchieveJoin(List<Students> students) {
        return new AchieveJoin(students);
    }

    /**
     * 构建学生回答的推送信息
     *
     * @param students
     * @return
     */
    private AchieveAnswer buildAchieveAnswer(List<Students> students) {
        return new AchieveAnswer(students);
    }

    /**
     * 构建学生回答的推送信息
     *
     * @param students
     * @return
     */
    private AchieveSurveyAnswer buildAchieveSurveyAnswer(List<Students> students) {
        return new AchieveSurveyAnswer(students);
    }

    /**
     * 构建学生回答的推送信息
     *
     * @param students
     * @return
     */
    private AchieveBrainstormAnswer buildAchieveBrainstormAnswer(List<Students> students) {
        return new AchieveBrainstormAnswer(students);
    }

    /**
     * 构建学生回答的推送信息
     *
     * @param students
     * @return
     */
    private AchieveTaskAnswer buildAchieveTaskAnswer(List<Students> students) {
        return new AchieveTaskAnswer(students);
    }

    /**
     * 构建学生回答的推送信息
     *
     * @param students
     * @return
     */
    private AchieveBookAnswer buildAchieveBookAnswer(List<Students> students) {
        return new AchieveBookAnswer(students);
    }


    /**
     * 获取互动的课堂学生信息
     *
     * @param uCircle
     * @return
     */
    private List<Students> interactStudents(String uCircle) {
        return interact.getSets(raiseKey(uCircle))
                .stream()
                .map(studentsService::findStudentsBrief)
                .collect(Collectors.toList());
    }

    /**
     * 查找互动学生信息
     *
     * @param uCircle
     * @return
     */
    private List<Students> findInteractiveStudents(String uCircle) {

        return interact.getSets(interactiveClassKey(uCircle))
                .stream()
                .map(id -> studentsService.findStudentsBrief(id))
                .collect(Collectors.toList());
    }


    /**
     * 查找学生的回答信息
     *
     * @param circleId
     * @param examineeId
     * @param questionId
     * @return
     */
    private Object findAskAnswer(final String circleId, final String examineeId, final String questionId, final QuestionType type) {

        if (type.equals(QuestionType.BigQuestion)) {
            Query query = Query.query(
                    Criteria.where("circleId").is(circleId)
                            .and("questionId").is(questionId)
                            .and("examineeId").is(examineeId));

            return mongoTemplate.findOne(query, AskAnswer.class);
        }

        Query query = Query.query(
                Criteria.where("circleId").is(circleId)
                        .and("examineeId").is(examineeId)
                        .and("libraryType").is(type.name()));
        ActivityAskAnswer activityAskAnswer = mongoTemplate.findOne(query, ActivityAskAnswer.class);
        return activityAskAnswerResults(activityAskAnswer);
    }

    private Object activityAskAnswerResults(ActivityAskAnswer activityAskAnswer) {
        if (activityAskAnswer == null) {
            return null;
        }
        return activityAskAnswer.getAnswList();
    }

    /**
     * 获取举手信息
     *
     * @param uid
     * @return
     */
    public AchieveRaise achieveRaise(String uid) {

        String uCircle = interact.uidCircle(uid);
        String uRandom = interact.uidRandom(uid);
        if (uCircle == null || uRandom == null) {
            return null;
        }
        String askKey = CLASSROOM_ASK_QUESTIONS_ID.concat(QuestionType.BigQuestion.name()).concat(uCircle);

        List<Students> uids = interactStudents(uCircle);
        if (uids.size() > 0 && interact.raiseDistinct(raiseDistinctKey(uCircle, uRandom), askKey, uids.size())) {
            return buildAchieveRaise(uids);
        } else {
            return null;
        }
    }

    /**
     * 主动推送 加入课堂的学生
     *
     * @param uid
     * @return
     */
    public AchieveJoin achieveInteractiveStudents(String uid) {

        String uCircle = interact.uidCircle(uid);
        String uRandom = interact.uidRandom(uid);
        if (uCircle == null || uRandom == null) {
            return null;
        }
        String askKey = CLASSROOM_ASK_QUESTIONS_ID.concat(QuestionType.BigQuestion.name()).concat(uCircle);
        //查找互动学生信息
        List<Students> uids = findInteractiveStudents(uCircle);
        if (uids.size() > 0 &&
                //学生加入信息去重
                interact.joinDistinct(joinDistinctKey(uCircle, uRandom), askKey, uids.size())) {
            return buildAchieveJoin(uids);
        } else {
            return null;
        }

    }
}
