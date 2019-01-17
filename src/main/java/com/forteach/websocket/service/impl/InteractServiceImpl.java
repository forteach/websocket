package com.forteach.websocket.service.impl;

import com.forteach.websocket.domain.*;
import com.forteach.websocket.repository.BigQuestionRepository;
import com.forteach.websocket.repository.BrainstormQuestionRepository;
import com.forteach.websocket.repository.SurveyQuestionRepository;
import com.forteach.websocket.repository.TaskQuestionRepository;
import com.forteach.websocket.service.InteractService;
import com.forteach.websocket.service.RedisInteract;
import com.forteach.websocket.service.StudentsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.forteach.websocket.common.Dic.*;
import static com.forteach.websocket.common.KeyStorage.*;
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
    private BigQuestionRepository bigQuestionRepository;

    @Resource
    private TaskQuestionRepository taskQuestionRepository;

    @Resource
    private SurveyQuestionRepository surveyQuestionRepository;

    @Resource
    private BrainstormQuestionRepository brainstormQuestionRepository;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private StudentsService studentsService;

    @Resource
    private RedisInteract interact;

    /**
     * 获取课堂交互信息
     *
     * @return
     */
    @Override
    public List<ToPush> obtain() {

        Set<String> uid = interact.getSets(INTERACTION_UID_SET_PREFIX);
        if (uid != null && uid.size() > 0) {
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
        String uType = interact.uidType(uid);
        if (SUBSCRIBE_USER_STUDENT.equals(uType)) {
            return ToPush.builder()
                    .uid(uid)
                    .askQuestion(achieveQuestion(uid))
                    .askSurvey(achieveSurvey(uid))
                    .askBrainstorm(achieveBrainstorm(uid))
                    .askTask(achieveTask(uid))
                    .build();
        } else {
            return ToPush.builder()
                    .uid(uid)
                    .achieveAnswer(achieveAnswer(uid))
                    .achieveRaise(achieveRaise(uid))
                    .achieveJoin(achieveInteractiveStudents(uid))
                    .build();
        }
    }

    /**
     * 获取需要推送的获取问题
     *
     * @param uid
     * @return
     */
    private AskQuestion achieveQuestion(String uid) {

        String uCircle = interact.uidCircle(uid);
        String askKey = CLASSROOM_ASK_QUESTIONS_ID.concat(QuestionType.BigQuestion.name()).concat(uCircle);
        String questionId = interact.askQuestionId(askKey);
        if (questionId == null) {
            return null;
        }
        String uRandom = interact.uidRandom(uid);
        String uDistinctKey = askQuDistinctKey(uCircle, uid, questionId, uRandom, QuestionType.BigQuestion);
        String cut = interact.askQuestionCut(askKey);
        String category = interact.askCategoryType(askKey);
        String interactive = interact.askInteractiveType(askKey);

        OptQuestion optQuestion = getQuestion(askKey, uid, category, interactive);

        if (optQuestion != null && interact.distinctKeyIsEmpty(uDistinctKey, askKey, optQuestion.getSelected())) {
            return buildAskQuestion(cut, optQuestion, interactive);
        } else {
            return null;
        }
    }

    /**
     * 获取需要推送的任务
     *
     * @param uid
     * @return
     */
    private AskTask achieveTask(String uid) {
        //获取
        String uCircle = interact.uidCircle(uid);
        String askKey = CLASSROOM_ASK_QUESTIONS_ID.concat(QuestionType.TaskQuestion.name()).concat(uCircle);
        String questionId = interact.askQuestionId(askKey);
        if (questionId == null) {
            return null;
        }
        String[] questionIds = questionId.split(",");
        String uRandom = interact.uidRandom(uid);
        String uDistinctKey = askQuDistinctKey(uCircle, uid, questionId, uRandom, QuestionType.TaskQuestion);
        String cut = interact.askQuestionCut(askKey);
        String category = interact.askCategoryType(askKey);

        OptQuestionList<TaskQuestion> questionList = getTaskQuestionList(askKey, uid, category, questionIds);

        if (questionList != null && interact.distinctKeyIsEmpty(uDistinctKey, askKey, questionList.getSelected())) {
            return buildAskTask(cut, questionList);
        } else {
            return null;
        }
    }

    /**
     * 获取需要推送的头脑风暴
     *
     * @param uid
     * @return
     */
    private AskBrainstorm achieveBrainstorm(String uid) {
        //获取
        String uCircle = interact.uidCircle(uid);
        String askKey = CLASSROOM_ASK_QUESTIONS_ID.concat(QuestionType.BrainstormQuestion.name()).concat(uCircle);
        String questionId = interact.askQuestionId(askKey);
        if (questionId == null) {
            return null;
        }
        String[] questionIds = questionId.split(",");
        String uRandom = interact.uidRandom(uid);
        String uDistinctKey = askQuDistinctKey(uCircle, uid, questionId, uRandom, QuestionType.BrainstormQuestion);
        String cut = interact.askQuestionCut(askKey);
        String category = interact.askCategoryType(askKey);

        OptQuestionList<BrainstormQuestion> questionList = getBrainstormQuestionList(askKey, uid, category, questionIds);

        if (questionList != null && interact.distinctKeyIsEmpty(uDistinctKey, askKey, questionList.getSelected())) {
            return buildAskBrainstorm(cut, questionList);
        } else {
            return null;
        }
    }

    /**
     * 获取需要推送的问卷
     *
     * @param uid
     * @return
     */
    private AskSurvey achieveSurvey(String uid) {
        //获取
        String uCircle = interact.uidCircle(uid);
        String askKey = CLASSROOM_ASK_QUESTIONS_ID.concat(QuestionType.SurveyQuestion.name()).concat(uCircle);
        String questionId = interact.askQuestionId(askKey);
        if (questionId == null) {
            return null;
        }
        String[] questionIds = questionId.split(",");
        String uRandom = interact.uidRandom(uid);
        String uDistinctKey = askQuDistinctKey(uCircle, uid, questionId, uRandom, QuestionType.SurveyQuestion);
        String cut = interact.askQuestionCut(askKey);
        String category = interact.askCategoryType(askKey);

        OptQuestionList<SurveyQuestion> questionList = getSurveyQuestionList(askKey, uid, category, questionIds);

        if (questionList != null && interact.distinctKeyIsEmpty(uDistinctKey, askKey, questionList.getSelected())) {
            return buildAskSurvey(cut, questionList);
        } else {
            return null;
        }
    }

    /**
     * 获取举手信息
     *
     * @param uid
     * @return
     */
    private AchieveRaise achieveRaise(String uid) {

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
    private AchieveJoin achieveInteractiveStudents(String uid) {

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


    /**
     * 主动推送给教师 当前问题的回答情况
     *
     * @return
     */
    private AchieveAnswer achieveAnswer(String uid) {

        String uCircle = interact.uidCircle(uid);
        String uRandom = interact.uidRandom(uid);
        String askKey = CLASSROOM_ASK_QUESTIONS_ID.concat(uCircle);
        String questionId = interact.askQuestionId(askKey);
        if (questionId == null) {
            return null;
        }
        if (!interact.untitled(askKey)) {
            return null;
        }

        if (interact.answerDistinct(getAnswDistinctKey(uCircle, uRandom), examineeIsReplyKey(uCircle), askKey)) {
            List<Students> students = peopleAnswer(uCircle, questionId, askKey);
            return buildAchieveAnswer(students);
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
    private List<Students> peopleAnswer(String uCircle, String questionId, String askKey) {
        return interact.getAnswerStudent(askKey).stream().map(id -> {
            boolean flag = interact.isMember(examineeIsReplyKey(uCircle), id);
            AskAnswer answ = findAskAnswer(uCircle, id, questionId);
            Students student = studentsService.findStudentsBrief(id);
            if (flag) {
                return new CircleAnswer(student, ASK_CIRCLE_ANSWER_DID, answ);
            } else {
                return new CircleAnswer(student, ASK_CIRCLE_ANSWER_ALREADY, new AskAnswer());
            }
        }).collect(Collectors.toList());
    }


    /**
     * 获取uid的问题
     *
     * @param askKey
     * @param uid
     * @param interactive
     * @return
     */
    private OptQuestion getQuestion(String askKey, String uid, String category, String interactive) {
        switch (category) {
            case CATEGORY_PEOPLE:
                return askPeople(askKey, uid, interactive);
            case CATEGORY_TEAM:
                return null;
            default:
                log.error("获取 getQuestion 信息错误 非法参数 错误的数据类型");
                return null;
        }
    }

    /**
     * 获取uid的问题
     *
     * @param askKey
     * @param uid
     * @param
     * @return
     */
    private OptQuestionList<SurveyQuestion> getSurveyQuestionList(String askKey, String uid, String category, String[] questionIds) {
        switch (category) {
            case CATEGORY_PEOPLE:
                return selectedSurveyOptListPeople(askKey, uid, questionIds);
            case CATEGORY_TEAM:
                return null;
            default:
                log.error("获取 getSurveyQuestionList 信息错误 非法参数 错误的数据类型");
                return null;
        }
    }


    /**
     * 获取uid的问题
     *
     * @param askKey
     * @param uid
     * @param
     * @return
     */
    private OptQuestionList<BrainstormQuestion> getBrainstormQuestionList(String askKey, String uid, String category, String[] questionIds) {
        switch (category) {
            case CATEGORY_PEOPLE:
                return selectedBrainstormOptListPeople(askKey, uid, questionIds);
            case CATEGORY_TEAM:
                return null;
            default:
                log.error("获取 getBrainstormQuestionList 信息错误 非法参数 错误的数据类型");
                return null;
        }
    }

    /**
     * 获取uid的问题
     *
     * @param askKey
     * @param uid
     * @param
     * @return
     */
    private OptQuestionList<TaskQuestion> getTaskQuestionList(String askKey, String uid, String category, String[] questionIds) {
        switch (category) {
            case CATEGORY_PEOPLE:
                return selectedTaskOptListPeople(askKey, uid, questionIds);
            case CATEGORY_TEAM:
                return null;
            default:
                log.error("获取 getTaskQuestionList 信息错误 非法参数 错误的数据类型");
                return null;
        }
    }


    /**
     * 个人对象 返回题目
     *
     * @param uid
     * @return
     */
    private OptQuestion askPeople(String askKey, String uid, String interactive) {
        switch (interactive) {
            case ASK_INTERACTIVE_RACE:
                return selected(selectQuestion(askKey, uid));
            case ASK_INTERACTIVE_RAISE:
                return raiseSelected(askKey, uid, findBigQuestion(askKey));
            case ASK_INTERACTIVE_SELECT:
                return selected(selectQuestion(askKey, uid));
            case ASK_INTERACTIVE_VOTE:
                return null;
            default:
                log.error(" askPeople 非法参数 错误的数据类型");
                return null;
        }
    }


    /**
     * 个人对象 返回题目集
     *
     * @param uid
     * @return
     */
    private OptQuestionList<SurveyQuestion> selectedSurveyOptListPeople(String askKey, String uid, String[] questionIds) {

        List<SurveyQuestion> list = selectSurveyQuestion(askKey, uid, questionIds);
        if (list == null) {
            return null;
        } else {
            return new OptQuestionList<>(ASK_QUESTIONS_SELECTED, list);
        }

    }

    /**
     * 个人对象 返回题目集
     *
     * @param uid
     * @return
     */
    private OptQuestionList<BrainstormQuestion> selectedBrainstormOptListPeople(String askKey, String uid, String[] questionIds) {

        List<BrainstormQuestion> list = selectBrainstormQuestion(askKey, uid, questionIds);
        if (list == null) {
            return null;
        } else {
            return new OptQuestionList<>(ASK_QUESTIONS_SELECTED, list);
        }

    }

    /**
     * 个人对象 返回题目集
     *
     * @param uid
     * @return
     */
    private OptQuestionList<TaskQuestion> selectedTaskOptListPeople(String askKey, String uid, String[] questionIds) {

        List<TaskQuestion> list = selectTaskQuestion(askKey, uid, questionIds);
        if (list == null) {
            return null;
        } else {
            return new OptQuestionList<>(ASK_QUESTIONS_SELECTED, list);
        }

    }

    /**
     * 被选中的学生获得问题
     *
     * @param askKey
     * @param uid
     * @return
     */
    private BigQuestion selectQuestion(String askKey, String uid) {
        if (interact.selectVerify(askKey, uid)) {
            return findBigQuestion(askKey);
        } else {
            return null;
        }
    }

    /**
     * 被选中的学生获得问卷
     *
     * @param askKey
     * @param uid
     * @return
     */
    private List<SurveyQuestion> selectSurveyQuestion(String askKey, String uid, String[] questionIds) {
        if (interact.selectVerify(askKey, uid)) {
            return (List<SurveyQuestion>) surveyQuestionRepository.findAllById(Arrays.asList(questionIds));
        } else {
            return null;
        }
    }

    /**
     * 被选中的学生获得问卷
     *
     * @param askKey
     * @param uid
     * @return
     */
    private List<BrainstormQuestion> selectBrainstormQuestion(String askKey, String uid, String[] questionIds) {
        if (interact.selectVerify(askKey, uid)) {
            return (List<BrainstormQuestion>) brainstormQuestionRepository.findAllById(Arrays.asList(questionIds));
        } else {
            return null;
        }
    }

    /**
     * 被选中的学生获得问卷
     *
     * @param askKey
     * @param uid
     * @return
     */
    private List<TaskQuestion> selectTaskQuestion(String askKey, String uid, String[] questionIds) {
        if (interact.selectVerify(askKey, uid)) {
            return (List<TaskQuestion>) taskQuestionRepository.findAllById(Arrays.asList(questionIds));
        } else {
            return null;
        }
    }

    /**
     * 封装是否能够回答
     *
     * @param bigQuestion
     * @return
     */
    private OptQuestion selected(BigQuestion bigQuestion) {
        return new OptQuestion(ASK_QUESTIONS_SELECTED, bigQuestion);
    }

    /**
     * 封装是否能够回答
     *
     * @param bigQuestion
     * @return
     */
    private OptQuestion raiseSelected(String askKey, String uid, BigQuestion bigQuestion) {
        if (interact.selectVerify(askKey, uid)) {
            return new OptQuestion(ASK_QUESTIONS_SELECTED, bigQuestion);
        } else {
            return new OptQuestion(ASK_QUESTIONS_UN_SELECTED, bigQuestion);
        }
    }

    /**
     * 构建提问问题返回值
     *
     * @param cut
     * @param optQuestion
     * @param interactive
     * @return
     */
    private AskQuestion buildAskQuestion(String cut, OptQuestion optQuestion, String interactive) {
        if (optQuestion != null) {
            return new AskQuestion<>(cut, optQuestion, interactive);
        } else {
            return null;
        }
    }

    /**
     * 构建任务返回值
     *
     * @param cut
     * @param questionList
     * @return
     */
    private AskTask buildAskTask(String cut, OptQuestionList<TaskQuestion> questionList) {
        if (questionList != null) {
            return new AskTask<>(cut, questionList.getList(), questionList.getSelected());
        } else {
            return null;
        }
    }

    /**
     * 构建头脑风暴返回值
     *
     * @param cut
     * @param questionList
     * @return
     */
    private AskBrainstorm buildAskBrainstorm(String cut, OptQuestionList<BrainstormQuestion> questionList) {
        if (questionList != null) {
            return new AskBrainstorm<>(cut, questionList.getList(), questionList.getSelected());
        } else {
            return null;
        }
    }

    /**
     * 构建问卷返回值
     *
     * @param cut
     * @param questionList
     * @return
     */
    private AskSurvey buildAskSurvey(String cut, OptQuestionList<SurveyQuestion> questionList) {
        if (questionList != null) {
            return new AskSurvey<>(cut, questionList.getList(), questionList.getSelected());
        } else {
            return null;
        }
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
     * 获得当前课堂的问题
     *
     * @param askKey
     * @return
     */
    private BigQuestion findBigQuestion(final String askKey) {

        String questionId = interact.askQuestionId(askKey);
        if (questionId == null) {
            return null;
        }
        return bigQuestionRepository.findById(questionId).get();
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
    private AskAnswer findAskAnswer(final String circleId, final String examineeId, final String questionId) {

        Query query = Query.query(
                Criteria.where("circleId").in(circleId)
                        .and("questionId").in(questionId)
                        .and("examineeId").in(examineeId));

        return mongoTemplate.findOne(query, AskAnswer.class);
    }


}
