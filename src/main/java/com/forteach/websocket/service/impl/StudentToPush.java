package com.forteach.websocket.service.impl;

import com.forteach.websocket.domain.*;
import com.forteach.websocket.repository.BigQuestionRepository;
import com.forteach.websocket.repository.BrainstormQuestionRepository;
import com.forteach.websocket.repository.SurveyQuestionRepository;
import com.forteach.websocket.repository.TaskQuestionRepository;
import com.forteach.websocket.service.RedisInteract;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import static com.forteach.websocket.common.Dic.*;
import static com.forteach.websocket.common.KeyStorage.askQuDistinctKey;

/**
 * @Description:推送给学生
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/20  14:29
 */
@Slf4j
@Component
public class StudentToPush {

    @Resource
    private RedisInteract interact;

    @Resource
    private BigQuestionRepository bigQuestionRepository;

    @Resource
    private TaskQuestionRepository taskQuestionRepository;

    @Resource
    private SurveyQuestionRepository surveyQuestionRepository;

    @Resource
    private BrainstormQuestionRepository brainstormQuestionRepository;

    /**
     * 获取需要推送的获取问题
     *
     * @param uid
     * @return
     */
    public AskQuestion achieveQuestion(String uid) {
        //Circleid,questid,studentid
//        if (log.isDebugEnabled()){
//            log.debug("获取需要推送的获取问题 参数　==> uid : {}", uid);
//        }
        //班级信息ID
        String uCircle = interact.uidCircle(uid);
            String uRandom = interact.uidRandom(uid);
            if (uCircle == null || uRandom == null) {
                return null;
            }
            //获得题目提问的Resdis键值
        String askKey = CLASSROOM_ASK_QUESTIONS_ID.concat(uCircle).concat(QuestionType.TiWen.name());
            //获得题目编号
        String questionId = interact.askQuestionId(askKey);
        if (questionId == null) {
            log.debug("获取需要推送的问题为NULL 参数　==> uid : {}", uid);
            return null;
        }
        //-------------------------------------------------------
        //获取学生 获取big question 的去重key
        String uDistinctKey = askQuDistinctKey(uCircle, uid, questionId, uRandom, QuestionType.TiWen);

        String cut = interact.askQuestionCut(askKey);

        String category = interact.askCategoryType(askKey);//获得个人回答还是组回答
        String interactive = interact.askInteractiveType(askKey);//获得题目回答方式
        //创建题目信息
            OptQuestion optQuestion = getQuestion(askKey, uid, category, interactive);

        if (log.isDebugEnabled() && optQuestion != null){
            log.debug("optQuestion : {}", optQuestion.toString());
        }
        //是否已经推送过该信息
        if (optQuestion != null && interact.distinctKeyIsEmpty(uDistinctKey, askKey, optQuestion.getSelected())) {

            return buildAskQuestion(cut, optQuestion, interactive, category);
        } else {
            return null;
        }
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
                //个人回答
                return askPeople(askKey, uid, interactive);
            case CATEGORY_TEAM:
                //组回答
                return askTeam(askKey, uid, interactive);
            default:
                log.error("获取 getQuestion 信息错误 非法参数 错误的数据类型");
                return null;
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
    private AskQuestion buildAskQuestion(String cut, OptQuestion optQuestion, String interactive, String category) {
        if (optQuestion != null) {
            return new AskQuestion<>(cut, optQuestion, interactive, category);
        } else {
            return null;
        }
    }

    /**
     * 个人对象 返回题目
     *
     * @param askKey
     * @param uid
     * @param interactive
     * @return
     */
    private OptQuestion askPeople(String askKey, String uid, String interactive) {
        if (log.isDebugEnabled()){
//            log.debug("个人对象 返回题目 参数 ==> askKey : {}, uid : {}, interactive : {}", askKey, uid, interactive);
        }
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
     * 小组对象 返回题目
     *
     * @param askKey
     * @param uid
     * @param interactive
     * @return
     */
    private OptQuestion askTeam(String askKey, String uid, String interactive) {
        switch (interactive) {
            case ASK_INTERACTIVE_RACE:
                return selected(selectTeamQuestion(askKey, uid));
            case ASK_INTERACTIVE_RAISE:
                return raiseSelectedTeam(askKey, uid, findBigQuestion(askKey));
            case ASK_INTERACTIVE_SELECT:
                return selected(selectTeamQuestion(askKey, uid));
            case ASK_INTERACTIVE_VOTE:
                return null;
            default:
                log.error(" askTeam 非法参数 错误的数据类型");
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
     * 封装是否能够回答
     *
     * @param bigQuestion
     * @return
     */
    private OptQuestion raiseSelectedTeam(String askKey, String uid, BigQuestion bigQuestion) {
        if (interact.selectTeamVerify(askKey, uid)) {
            return new OptQuestion(ASK_QUESTIONS_SELECTED, bigQuestion);
        } else {
            return new OptQuestion(ASK_QUESTIONS_UN_SELECTED, bigQuestion);
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
     * 获取提问的team问题
     *
     * @param askKey
     * @param uid
     * @return
     */
    private BigQuestion selectTeamQuestion(String askKey, String uid) {
        if (interact.selectTeamVerify(askKey, uid)) {
            return findBigQuestion(askKey);
        } else {
            return null;
        }
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
     * 获取需要推送的问卷
     *
     * @param uid
     * @return
     */
    public AskSurvey achieveSurvey(String uid) {
        //获取
        String uCircle = interact.uidCircle(uid);
        String uRandom = interact.uidRandom(uid);

        if (uCircle == null || uRandom == null) {
            return null;
        }
        String askKey = CLASSROOM_ASK_QUESTIONS_ID.concat(QuestionType.WenJuan.name()).concat(uCircle);
        String questionId = interact.askQuestionId(askKey);
        if (questionId == null) {
            return null;
        }
        String[] questionIds = questionId.split(",");
        String uDistinctKey = askQuDistinctKey(uCircle, uid, questionId, uRandom, QuestionType.WenJuan);
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
                return selectedSurveyOptListTeam(askKey, uid, questionIds);
            default:
                log.error("获取 getSurveyQuestionList 信息错误 非法参数 错误的数据类型");
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
    private OptQuestionList<SurveyQuestion> selectedSurveyOptListTeam(String askKey, String uid, String[] questionIds) {

        List<SurveyQuestion> list = selectSurveyQuestionTeam(askKey, uid, questionIds);
        if (list == null) {
            return null;
        } else {
            return new OptQuestionList<>(ASK_QUESTIONS_SELECTED, list);
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
    private List<SurveyQuestion> selectSurveyQuestionTeam(String askKey, String uid, String[] questionIds) {
        if (interact.selectTeamVerify(askKey, uid)) {
            return (List<SurveyQuestion>) surveyQuestionRepository.findAllById(Arrays.asList(questionIds));
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
     * 获取需要推送的头脑风暴
     *
     * @param uid
     * @return
     */
    public AskBrainstorm achieveBrainstorm(String uid) {
        //获取
        String uCircle = interact.uidCircle(uid);
        String uRandom = interact.uidRandom(uid);

        if (uCircle == null || uRandom == null) {
            return null;
        }
        String askKey = CLASSROOM_ASK_QUESTIONS_ID.concat(QuestionType.FengBao.name()).concat(uCircle);
        String questionId = interact.askQuestionId(askKey);
        if (questionId == null) {
            return null;
        }
        String[] questionIds = questionId.split(",");
        String uDistinctKey = askQuDistinctKey(uCircle, uid, questionId, uRandom, QuestionType.FengBao);
        String cut = interact.askQuestionCut(askKey);
        String category = interact.askCategoryType(askKey);
        //获取uid的问题
        OptQuestionList<BrainstormQuestion> questionList = getBrainstormQuestionList(askKey, uid, category, questionIds);

        if (questionList != null && interact.distinctKeyIsEmpty(uDistinctKey, askKey, questionList.getSelected())) {
            return buildAskBrainstorm(cut, questionList);
        } else {
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
                return selectedBrainstormOptListTeam(askKey, uid, questionIds);
            default:
                log.error("获取 getBrainstormQuestionList 信息错误 非法参数 错误的数据类型");
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
     * 个人对象 返回题目集
     *
     * @param uid
     * @return
     */
    private OptQuestionList<BrainstormQuestion> selectedBrainstormOptListPeople(String askKey, String uid, String[] questionIds) {
        //被选中的学生获得问卷
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
    private OptQuestionList<BrainstormQuestion> selectedBrainstormOptListTeam(String askKey, String uid, String[] questionIds) {
        //被选中的学生获得问卷
        List<BrainstormQuestion> list = selectBrainstormQuestionTeam(askKey, uid, questionIds);
        if (list == null) {
            return null;
        } else {
            return new OptQuestionList<>(ASK_QUESTIONS_SELECTED, list);
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
            //
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
    private List<BrainstormQuestion> selectBrainstormQuestionTeam(String askKey, String uid, String[] questionIds) {
        if (interact.selectTeamVerify(askKey, uid)) {
            return (List<BrainstormQuestion>) brainstormQuestionRepository.findAllById(Arrays.asList(questionIds));
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
    public AskTask achieveTask(String uid) {
        //获取
        String uCircle = interact.uidCircle(uid);
        String uRandom = interact.uidRandom(uid);

        if (uCircle == null || uRandom == null) {
            return null;
        }
        String askKey = CLASSROOM_ASK_QUESTIONS_ID.concat(QuestionType.RenWu.name()).concat(uCircle);
        String questionId = interact.askQuestionId(askKey);
        if (questionId == null) {
            return null;
        }
        String[] questionIds = questionId.split(",");
        String uDistinctKey = askQuDistinctKey(uCircle, uid, questionId, uRandom, QuestionType.RenWu);
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
     * 获取需要推送的任务
     *
     * @param uid
     * @return
     */
    public AskBook achieveBook(String uid) {
        //获取
        String uCircle = interact.uidCircle(uid);
        String uRandom = interact.uidRandom(uid);
        if (uCircle == null || uRandom == null) {
            return null;
        }
        String askKey = CLASSROOM_ASK_QUESTIONS_ID.concat(QuestionType.LianXi.name()).concat(uCircle);
        String questionId = interact.askQuestionId(askKey);
        if (questionId == null) {
            return null;
        }
        String[] questionIds = questionId.split(",");
        String uDistinctKey = askQuDistinctKey(uCircle, uid, questionId, uRandom, QuestionType.LianXi);
        String cut = interact.askQuestionCut(askKey);
        String category = interact.askCategoryType(askKey);

        OptQuestionList<BigQuestion> questionList = getBigQuestionList(askKey, uid, category, questionIds);

        if (questionList != null && interact.distinctKeyIsEmpty(uDistinctKey, askKey, questionList.getSelected())) {
            return buildAskBook(cut, questionList);
        } else {
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
                return selectedTaskOptListTeam(askKey, uid, questionIds);
            default:
                log.error("获取 getTaskQuestionList 信息错误 非法参数 错误的数据类型");
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
    private OptQuestionList<BigQuestion> getBigQuestionList(String askKey, String uid, String category, String[] questionIds) {
        switch (category) {
            case CATEGORY_PEOPLE:
                return selectedBigQuestionOptListPeople(askKey, uid, questionIds);
            case CATEGORY_TEAM:
                return selectedBigQuestionOptListTeam(askKey, uid, questionIds);
            default:
                log.error("获取 getTaskQuestionList 信息错误 非法参数 错误的数据类型");
                return null;
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
     * 个人对象 返回题目集
     *
     * @param uid
     * @return
     */
    private OptQuestionList<TaskQuestion> selectedTaskOptListTeam(String askKey, String uid, String[] questionIds) {

        List<TaskQuestion> list = selectTaskQuestionTeam(askKey, uid, questionIds);
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
    private OptQuestionList<BigQuestion> selectedBigQuestionOptListPeople(String askKey, String uid, String[] questionIds) {

        List<BigQuestion> list = selectBigQuestion(askKey, uid, questionIds);
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
    private OptQuestionList<BigQuestion> selectedBigQuestionOptListTeam(String askKey, String uid, String[] questionIds) {

        List<BigQuestion> list = selectBigQuestionTeam(askKey, uid, questionIds);
        if (list == null) {
            return null;
        } else {
            return new OptQuestionList<>(ASK_QUESTIONS_SELECTED, list);
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
     * 被选中的学生获得问卷
     *
     * @param askKey
     * @param uid
     * @return
     */
    private List<TaskQuestion> selectTaskQuestionTeam(String askKey, String uid, String[] questionIds) {
        if (interact.selectTeamVerify(askKey, uid)) {
            return (List<TaskQuestion>) taskQuestionRepository.findAllById(Arrays.asList(questionIds));
        } else {
            return null;
        }
    }

    /**
     * 被选中的学生获得练习册
     *
     * @param askKey
     * @param uid
     * @return
     */
    private List<BigQuestion> selectBigQuestion(String askKey, String uid, String[] questionIds) {
        if (interact.selectVerify(askKey, uid)) {
            return (List<BigQuestion>) bigQuestionRepository.findAllById(Arrays.asList(questionIds));
        } else {
            return null;
        }
    }

    /**
     * 被选中的学生获得练习册
     *
     * @param askKey
     * @param uid
     * @return
     */
    private List<BigQuestion> selectBigQuestionTeam(String askKey, String uid, String[] questionIds) {
        if (interact.selectTeamVerify(askKey, uid)) {
            return (List<BigQuestion>) bigQuestionRepository.findAllById(Arrays.asList(questionIds));
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
     * 构建任务返回值
     *
     * @param cut
     * @param questionList
     * @return
     */
    private AskBook buildAskBook(String cut, OptQuestionList<BigQuestion> questionList) {
        if (questionList != null) {
            return new AskBook<>(cut, questionList.getList(), questionList.getSelected());
        } else {
            return null;
        }
    }
}
