package com.forteach.websocket.service.impl;

import com.forteach.websocket.domain.*;
import com.forteach.websocket.service.RedisInteract;
import com.forteach.websocket.service.StudentsService;
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
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/20  14:30
 */
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
        String askKey = CLASSROOM_ASK_QUESTIONS_ID.concat(QuestionType.SurveyQuestion.name()).concat(uCircle);
        String questionId = interact.askQuestionId(askKey);
        if (questionId == null) {
            return null;
        }
        if (!interact.untitled(askKey)) {
            return null;
        }
        if (interact.answerDistinct(getAnswDistinctKey(uCircle, uRandom), examineeIsReplyKey(QuestionType.SurveyQuestion, uCircle), askKey)) {
            List<Students> students = peopleAnswer(uCircle, questionId, askKey, QuestionType.SurveyQuestion);
            return buildAchieveSurveyAnswer(students);
        } else {
            return null;
        }
    }

    public AchieveBrainstormAnswer achieveBrainstormAnswer(String uid) {
        String uCircle = interact.uidCircle(uid);
        String uRandom = interact.uidRandom(uid);
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
            boolean flag = interact.isMember(examineeIsReplyKey(type, uCircle), id);
            Object answ = findAskAnswer(uCircle, id, questionId, type);
            Students student = studentsService.findStudentsBrief(id);
            if (flag) {
                return new CircleAnswer(student, ASK_CIRCLE_ANSWER_DID, answ);
            } else {
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
                    Criteria.where("circleId").in(circleId)
                            .and("questionId").in(questionId)
                            .and("examineeId").in(examineeId));

            return mongoTemplate.findOne(query, AskAnswer.class);
        }

        Query query = Query.query(
                Criteria.where("circleId").is(circleId)
                        .and("examineeId").is(examineeId)
                        .and("libraryType").is(type));
        return mongoTemplate.findOne(query, ActivityAskAnswer.class).getAnswList();
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
        String askKey = CLASSROOM_ASK_QUESTIONS_ID.concat(QuestionType.BigQuestion.name()).concat(uCircle);

        List<Students> uids = findInteractiveStudents(uCircle);
        if (uids.size() > 0 && interact.joinDistinct(joinDistinctKey(uCircle, uRandom), askKey, uids.size())) {
            return buildAchieveJoin(uids);
        } else {
            return null;
        }

    }
}
