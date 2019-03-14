package com.forteach.websocket.service.student.push;

import com.alibaba.fastjson.JSON;
import com.forteach.websocket.common.BigQueKey;
import com.forteach.websocket.common.Dic;
import com.forteach.websocket.common.QuestionType;
import com.forteach.websocket.domain.*;
import com.forteach.websocket.repository.BigQuestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import static com.forteach.websocket.common.Dic.*;
import static com.forteach.websocket.service.WsService.SESSION_MAP;

/**
 * @Description:推送给学生
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/20  14:29
 */
@Slf4j
@Component
public class TiWenPush {

    //学生交互操作类
    @Resource
    private StuInteractImpl stuInteract;

    /**
     *学生提问推送信息
     * @param circleId
     * @return
     */
    public List<ToStudentPush> tiWenStudent(final String circleId){

        //获得提问方式的题目编号
        final String questId=stuInteract.getNowQuestId(QuestionType.TiWen,circleId, Dic.ASK_INTERACTIVE_SELECT);
        //获得当前题目选中的学生
        final String stus= stuInteract.getQuestSelectStu(circleId);

        //获得当前题目的交互方式
        final String interactive=stuInteract.getNowQuestInteractive(circleId);  //交互方式  选人、举手、抢答
        //暂时设定，需要从redis里面去除该值
        final String category=stuInteract.getNowQuestCategory(circleId);  //小组 个人

        //根据所选的学生，对比Session数据是否在线，并获得学生推送详情
        return Arrays.asList(stus.split(",")).stream()
                .filter(id -> null != SESSION_MAP.get(id))
                .filter(id -> SESSION_MAP.get(id).isOpen())
                //创建推送数据
                .map(uid->TStudentToPush(uid,questId, interactive, category))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 推送学生数据对象构造
     ** @param uid 学生编号
     * @param questid 题目编号
     * @param interactive  交互方式  选人、举手、抢答
     * @param category  小组 个人
     * @return
     */
    private ToStudentPush TStudentToPush(String uid,String questid,String interactive,String category) {
                //是学生推送学生信息
                return ToStudentPush.builder()
                        .uid(uid)
                        //提问问题
                        .askQuestion(achieveQuestion(questid,interactive,category))
                        .build();
    }

    /**
     *
    // * @param circleid  课堂编号
     * @param interactive     题目的回答方式
     * @param questid   提问的问题编号
     * @param category   个人、小组回答
     * @return  返回题目信息
     */
    public AskQuestion achieveQuestion(String questid,String interactive,String category){
        //前端发送过来的时间阀值
        String cut="";
        //创建题目信息
        OptQuestion optQuestion = getQuestion(category, questid,  interactive);
        //返回提问信息
        return buildAskQuestion(cut, optQuestion, interactive, category);
    }


    /**
     * 获取uid的问题
     *
     * @param interactive
     * @return
     */
    private OptQuestion getQuestion(String category,String questionId, String interactive) {
        switch (category) {
            case CATEGORY_PEOPLE:
                //个人回答
                return askPeople(questionId, interactive);
            case CATEGORY_TEAM:
                //组回答
                return null;//askTeam(askKey, uid, interactive);
            default:
                log.error("获取 getQuestion 信息错误 非法参数 错误的数据类型");
                return null;
        }
    }

    /**
     * 个人对象 返回题目
     *
     * @param questionId
     * @param interactive
     * @return
     */
    private OptQuestion askPeople(String questionId, String interactive) {
        switch (interactive) {
            case ASK_INTERACTIVE_RACE:
                return selected(stuInteract.getBigQuestion(questionId));
            case ASK_INTERACTIVE_RAISE:
                return null;//raiseSelected(askKey, uid, findBigQuestion(askKey));
            case ASK_INTERACTIVE_SELECT:
                return selected(stuInteract.getBigQuestion(questionId));
            case ASK_INTERACTIVE_VOTE:
                return null;
            default:
                log.error(" askPeople 非法参数 错误的数据类型");
                return null;
        }
    }

    /**
     * 选人方式题目推送
     *
     * @param bigQuestion
     * @return
     */
    private OptQuestion selected(BigQuestion bigQuestion) {
        return new OptQuestion(ASK_QUESTIONS_SELECTED, bigQuestion);
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



//----------------------------------------------------------------------------------------------------------------------------

//    /**
//     * 封装是否能够回答
//     *
//     * @param bigQuestion
//     * @return
//     */
//    private OptQuestion raiseSelected(String askKey,String uid, BigQuestion bigQuestion) {
//        if (interact.selectVerify(askKey, uid)) {
//            return new OptQuestion(ASK_QUESTIONS_SELECTED, bigQuestion);
//        } else {
//            return new OptQuestion(ASK_QUESTIONS_UN_SELECTED, bigQuestion);
//        }
//    }
//
//    /**
//     * 封装是否能够回答
//     *
//     * @param bigQuestion
//     * @return
//     */
//    private OptQuestion raiseSelectedTeam(String askKey,String uid, BigQuestion bigQuestion) {
//        if (interact.selectTeamVerify(askKey, uid)) {
//            return new OptQuestion(ASK_QUESTIONS_SELECTED, bigQuestion);
//        } else {
//            return new OptQuestion(ASK_QUESTIONS_UN_SELECTED, bigQuestion);
//        }
//    }
//    /**
//     * 获取需要推送的获取问题
//     *
//     * @param uid
//     * @return
//     */
//    public AskQuestion achieveQuestion(String uid) {
//        //Circleid,questid,studentid
////        if (log.isDebugEnabled()){
////            log.debug("获取需要推送的获取问题 参数　==> uid : {}", uid);
////        }
//        //班级信息ID
//        String uCircle = interact.uidCircle(uid);
//            String uRandom = interact.uidRandom(uid);
//            if (uCircle == null || uRandom == null) {
//                return null;
//            }
//            //获得题目提问的Resdis键值
//        String askKey = CLASSROOM_ASK_QUESTIONS_ID.concat(uCircle).concat(QuestionType.BigQuestion.name());
//            //获得题目编号
//        String questionId = interact.askQuestionId(askKey);
//        if (questionId == null) {
//            log.debug("获取需要推送的问题为NULL 参数　==> uid : {}", uid);
//            return null;
//        }
//        //-------------------------------------------------------
//        //获取学生 获取big question 的去重key
//        String uDistinctKey = askQuDistinctKey(uCircle, uid, questionId, uRandom, QuestionType.BigQuestion);
//
//        String cut = interact.askQuestionCut(askKey);
//
//        String category = interact.askCategoryType(askKey);//获得个人回答还是组回答
//        String interactive = interact.askInteractiveType(askKey);//获得题目回答方式
//        //创建题目信息
//            OptQuestion optQuestion = getQuestion(askKey, uid, category, interactive);
//
//        if (log.isDebugEnabled() && optQuestion != null){
//            log.debug("optQuestion : {}", optQuestion.toString());
//        }
//        //是否已经推送过该信息
//        if (optQuestion != null && interact.distinctKeyIsEmpty(uDistinctKey, askKey, optQuestion.getSelected())) {
//
//            return buildAskQuestion(cut, optQuestion, interactive, category);
//        } else {
//            return null;
//        }
//    }
//
//    //获得需要推送的问题
//
//
//
//
//

//
//
//
//    /**
//     * 小组对象 返回题目
//     *
//     * @param askKey
//     * @param uid
//     * @param interactive
//     * @return
//     */
//    private OptQuestion askTeam(String askKey, String uid, String interactive) {
//        switch (interactive) {
//            case ASK_INTERACTIVE_RACE:
//                return selected(selectTeamQuestion(askKey, uid));
//            case ASK_INTERACTIVE_RAISE:
//                return raiseSelectedTeam(askKey, uid, findBigQuestion(askKey));
//            case ASK_INTERACTIVE_SELECT:
//                return selected(selectTeamQuestion(askKey, uid));
//            case ASK_INTERACTIVE_VOTE:
//                return null;
//            default:
//                log.error(" askTeam 非法参数 错误的数据类型");
//                return null;
//        }
//    }
//
//
//

//
//
//
//    /**
//     * 获取提问的team问题
//     *
//     * @param askKey
//     * @param uid
//     * @return
//     */
//    private BigQuestion selectTeamQuestion(String askKey, String uid) {
//        if (interact.selectTeamVerify(askKey, uid)) {
//            return findBigQuestion(askKey);
//        } else {
//            return null;
//        }
//    }
//
//
//
//
//    /**
//     * 获取需要推送的问卷
//     *
//     * @param uid
//     * @return
//     */
//    public AskSurvey achieveSurvey(String uid) {
//        //获取
//        String uCircle = interact.uidCircle(uid);
//        String uRandom = interact.uidRandom(uid);
//
//        if (uCircle == null || uRandom == null) {
//            return null;
//        }
//        String askKey = CLASSROOM_ASK_QUESTIONS_ID.concat(QuestionType.SurveyQuestion.name()).concat(uCircle);
//        String questionId = interact.askQuestionId(askKey);
//        if (questionId == null) {
//            return null;
//        }
//        String[] questionIds = questionId.split(",");
//        String uDistinctKey = askQuDistinctKey(uCircle, uid, questionId, uRandom, QuestionType.SurveyQuestion);
//        String cut = interact.askQuestionCut(askKey);
//        String category = interact.askCategoryType(askKey);
//
//        OptQuestionList<SurveyQuestion> questionList = getSurveyQuestionList(askKey, uid, category, questionIds);
//
//        if (questionList != null && interact.distinctKeyIsEmpty(uDistinctKey, askKey, questionList.getSelected())) {
//            return buildAskSurvey(cut, questionList);
//        } else {
//            return null;
//        }
//    }
//
//    /**
//     * 获取uid的问题
//     *
//     * @param askKey
//     * @param uid
//     * @param
//     * @return
//     */
//    private OptQuestionList<SurveyQuestion> getSurveyQuestionList(String askKey, String uid, String category, String[] questionIds) {
//        switch (category) {
//            case CATEGORY_PEOPLE:
//                return selectedSurveyOptListPeople(askKey, uid, questionIds);
//            case CATEGORY_TEAM:
//                return selectedSurveyOptListTeam(askKey, uid, questionIds);
//            default:
//                log.error("获取 getSurveyQuestionList 信息错误 非法参数 错误的数据类型");
//                return null;
//        }
//    }
//
//    /**
//     * 个人对象 返回题目集
//     *
//     * @param uid
//     * @return
//     */
//    private OptQuestionList<SurveyQuestion> selectedSurveyOptListPeople(String askKey, String uid, String[] questionIds) {
//
//        List<SurveyQuestion> list = selectSurveyQuestion(askKey, uid, questionIds);
//        if (list == null) {
//            return null;
//        } else {
//            return new OptQuestionList<>(ASK_QUESTIONS_SELECTED, list);
//        }
//
//    }
//
//    /**
//     * 个人对象 返回题目集
//     *
//     * @param uid
//     * @return
//     */
//    private OptQuestionList<SurveyQuestion> selectedSurveyOptListTeam(String askKey, String uid, String[] questionIds) {
//
//        List<SurveyQuestion> list = selectSurveyQuestionTeam(askKey, uid, questionIds);
//        if (list == null) {
//            return null;
//        } else {
//            return new OptQuestionList<>(ASK_QUESTIONS_SELECTED, list);
//        }
//
//    }
//
//    /**
//     * 被选中的学生获得问卷
//     *
//     * @param askKey
//     * @param uid
//     * @return
//     */
//    private List<SurveyQuestion> selectSurveyQuestion(String askKey, String uid, String[] questionIds) {
//        if (interact.selectVerify(askKey, uid)) {
//            return (List<SurveyQuestion>) surveyQuestionRepository.findAllById(Arrays.asList(questionIds));
//        } else {
//            return null;
//        }
//    }
//
//    /**
//     * 被选中的学生获得问卷
//     *
//     * @param askKey
//     * @param uid
//     * @return
//     */
//    private List<SurveyQuestion> selectSurveyQuestionTeam(String askKey, String uid, String[] questionIds) {
//        if (interact.selectTeamVerify(askKey, uid)) {
//            return (List<SurveyQuestion>) surveyQuestionRepository.findAllById(Arrays.asList(questionIds));
//        } else {
//            return null;
//        }
//    }
//
//    /**
//     * 构建问卷返回值
//     *
//     * @param cut
//     * @param questionList
//     * @return
//     */
//    private AskSurvey buildAskSurvey(String cut, OptQuestionList<SurveyQuestion> questionList) {
//        if (questionList != null) {
//            return new AskSurvey<>(cut, questionList.getList(), questionList.getSelected());
//        } else {
//            return null;
//        }
//    }
//
//    /**
//     * 获取需要推送的头脑风暴
//     *
//     * @param uid
//     * @return
//     */
//    public AskBrainstorm achieveBrainstorm(String uid) {
//        //获取
//        String uCircle = interact.uidCircle(uid);
//        String uRandom = interact.uidRandom(uid);
//
//        if (uCircle == null || uRandom == null) {
//            return null;
//        }
//        String askKey = CLASSROOM_ASK_QUESTIONS_ID.concat(QuestionType.BrainstormQuestion.name()).concat(uCircle);
//        String questionId = interact.askQuestionId(askKey);
//        if (questionId == null) {
//            return null;
//        }
//        String[] questionIds = questionId.split(",");
//        String uDistinctKey = askQuDistinctKey(uCircle, uid, questionId, uRandom, QuestionType.BrainstormQuestion);
//        String cut = interact.askQuestionCut(askKey);
//        String category = interact.askCategoryType(askKey);
//        //获取uid的问题
//        OptQuestionList<BrainstormQuestion> questionList = getBrainstormQuestionList(askKey, uid, category, questionIds);
//
//        if (questionList != null && interact.distinctKeyIsEmpty(uDistinctKey, askKey, questionList.getSelected())) {
//            return buildAskBrainstorm(cut, questionList);
//        } else {
//            return null;
//        }
//    }
//
//    /**
//     * 获取uid的问题
//     *
//     * @param askKey
//     * @param uid
//     * @param
//     * @return
//     */
//    private OptQuestionList<BrainstormQuestion> getBrainstormQuestionList(String askKey, String uid, String category, String[] questionIds) {
//        switch (category) {
//            case CATEGORY_PEOPLE:
//                return selectedBrainstormOptListPeople(askKey, uid, questionIds);
//            case CATEGORY_TEAM:
//                return selectedBrainstormOptListTeam(askKey, uid, questionIds);
//            default:
//                log.error("获取 getBrainstormQuestionList 信息错误 非法参数 错误的数据类型");
//                return null;
//        }
//    }
//
//    /**
//     * 构建头脑风暴返回值
//     *
//     * @param cut
//     * @param questionList
//     * @return
//     */
//    private AskBrainstorm buildAskBrainstorm(String cut, OptQuestionList<BrainstormQuestion> questionList) {
//        if (questionList != null) {
//            return new AskBrainstorm<>(cut, questionList.getList(), questionList.getSelected());
//        } else {
//            return null;
//        }
//    }
//
//    /**
//     * 个人对象 返回题目集
//     *
//     * @param uid
//     * @return
//     */
//    private OptQuestionList<BrainstormQuestion> selectedBrainstormOptListPeople(String askKey, String uid, String[] questionIds) {
//        //被选中的学生获得问卷
//        List<BrainstormQuestion> list = selectBrainstormQuestion(askKey, uid, questionIds);
//        if (list == null) {
//            return null;
//        } else {
//            return new OptQuestionList<>(ASK_QUESTIONS_SELECTED, list);
//        }
//
//    }
//
//    /**
//     * 个人对象 返回题目集
//     *
//     * @param uid
//     * @return
//     */
//    private OptQuestionList<BrainstormQuestion> selectedBrainstormOptListTeam(String askKey, String uid, String[] questionIds) {
//        //被选中的学生获得问卷
//        List<BrainstormQuestion> list = selectBrainstormQuestionTeam(askKey, uid, questionIds);
//        if (list == null) {
//            return null;
//        } else {
//            return new OptQuestionList<>(ASK_QUESTIONS_SELECTED, list);
//        }
//
//    }
//
//    /**
//     * 被选中的学生获得问卷
//     *
//     * @param askKey
//     * @param uid
//     * @return
//     */
//    private List<BrainstormQuestion> selectBrainstormQuestion(String askKey, String uid, String[] questionIds) {
//        if (interact.selectVerify(askKey, uid)) {
//            //
//            return (List<BrainstormQuestion>) brainstormQuestionRepository.findAllById(Arrays.asList(questionIds));
//        } else {
//            return null;
//        }
//    }
//
//    /**
//     * 被选中的学生获得问卷
//     *
//     * @param askKey
//     * @param uid
//     * @return
//     */
//    private List<BrainstormQuestion> selectBrainstormQuestionTeam(String askKey, String uid, String[] questionIds) {
//        if (interact.selectTeamVerify(askKey, uid)) {
//            return (List<BrainstormQuestion>) brainstormQuestionRepository.findAllById(Arrays.asList(questionIds));
//        } else {
//            return null;
//        }
//    }
//
//    /**
//     * 获取需要推送的任务
//     *
//     * @param uid
//     * @return
//     */
//    public AskTask achieveTask(String uid) {
//        //获取
//        String uCircle = interact.uidCircle(uid);
//        String uRandom = interact.uidRandom(uid);
//
//        if (uCircle == null || uRandom == null) {
//            return null;
//        }
//        String askKey = CLASSROOM_ASK_QUESTIONS_ID.concat(QuestionType.TaskQuestion.name()).concat(uCircle);
//        String questionId = interact.askQuestionId(askKey);
//        if (questionId == null) {
//            return null;
//        }
//        String[] questionIds = questionId.split(",");
//        String uDistinctKey = askQuDistinctKey(uCircle, uid, questionId, uRandom, QuestionType.TaskQuestion);
//        String cut = interact.askQuestionCut(askKey);
//        String category = interact.askCategoryType(askKey);
//
//        OptQuestionList<TaskQuestion> questionList = getTaskQuestionList(askKey, uid, category, questionIds);
//
//        if (questionList != null && interact.distinctKeyIsEmpty(uDistinctKey, askKey, questionList.getSelected())) {
//            return buildAskTask(cut, questionList);
//        } else {
//            return null;
//        }
//    }
//
//    /**
//     * 获取需要推送的任务
//     *
//     * @param uid
//     * @return
//     */
//    public AskBook achieveBook(String uid) {
//        //获取
//        String uCircle = interact.uidCircle(uid);
//        String uRandom = interact.uidRandom(uid);
//        if (uCircle == null || uRandom == null) {
//            return null;
//        }
//        String askKey = CLASSROOM_ASK_QUESTIONS_ID.concat(QuestionType.ExerciseBook.name()).concat(uCircle);
//        String questionId = interact.askQuestionId(askKey);
//        if (questionId == null) {
//            return null;
//        }
//        String[] questionIds = questionId.split(",");
//        String uDistinctKey = askQuDistinctKey(uCircle, uid, questionId, uRandom, QuestionType.ExerciseBook);
//        String cut = interact.askQuestionCut(askKey);
//        String category = interact.askCategoryType(askKey);
//
//        OptQuestionList<BigQuestion> questionList = getBigQuestionList(askKey, uid, category, questionIds);
//
//        if (questionList != null && interact.distinctKeyIsEmpty(uDistinctKey, askKey, questionList.getSelected())) {
//            return buildAskBook(cut, questionList);
//        } else {
//            return null;
//        }
//    }
//
//    /**
//     * 获取uid的问题
//     *
//     * @param askKey
//     * @param uid
//     * @param
//     * @return
//     */
//    private OptQuestionList<TaskQuestion> getTaskQuestionList(String askKey, String uid, String category, String[] questionIds) {
//        switch (category) {
//            case CATEGORY_PEOPLE:
//                return selectedTaskOptListPeople(askKey, uid, questionIds);
//            case CATEGORY_TEAM:
//                return selectedTaskOptListTeam(askKey, uid, questionIds);
//            default:
//                log.error("获取 getTaskQuestionList 信息错误 非法参数 错误的数据类型");
//                return null;
//        }
//    }
//
//    /**
//     * 获取uid的问题
//     *
//     * @param askKey
//     * @param uid
//     * @param
//     * @return
//     */
//    private OptQuestionList<BigQuestion> getBigQuestionList(String askKey, String uid, String category, String[] questionIds) {
//        switch (category) {
//            case CATEGORY_PEOPLE:
//                return selectedBigQuestionOptListPeople(askKey, uid, questionIds);
//            case CATEGORY_TEAM:
//                return selectedBigQuestionOptListTeam(askKey, uid, questionIds);
//            default:
//                log.error("获取 getTaskQuestionList 信息错误 非法参数 错误的数据类型");
//                return null;
//        }
//    }
//
//    /**
//     * 个人对象 返回题目集
//     *
//     * @param uid
//     * @return
//     */
//    private OptQuestionList<TaskQuestion> selectedTaskOptListPeople(String askKey, String uid, String[] questionIds) {
//
//        List<TaskQuestion> list = selectTaskQuestion(askKey, uid, questionIds);
//        if (list == null) {
//            return null;
//        } else {
//            return new OptQuestionList<>(ASK_QUESTIONS_SELECTED, list);
//        }
//
//    }
//
//    /**
//     * 个人对象 返回题目集
//     *
//     * @param uid
//     * @return
//     */
//    private OptQuestionList<TaskQuestion> selectedTaskOptListTeam(String askKey, String uid, String[] questionIds) {
//
//        List<TaskQuestion> list = selectTaskQuestionTeam(askKey, uid, questionIds);
//        if (list == null) {
//            return null;
//        } else {
//            return new OptQuestionList<>(ASK_QUESTIONS_SELECTED, list);
//        }
//
//    }
//
//    /**
//     * 个人对象 返回题目集
//     *
//     * @param uid
//     * @return
//     */
//    private OptQuestionList<BigQuestion> selectedBigQuestionOptListPeople(String askKey, String uid, String[] questionIds) {
//
//        List<BigQuestion> list = selectBigQuestion(askKey, uid, questionIds);
//        if (list == null) {
//            return null;
//        } else {
//            return new OptQuestionList<>(ASK_QUESTIONS_SELECTED, list);
//        }
//
//    }
//
//    /**
//     * 个人对象 返回题目集
//     *
//     * @param uid
//     * @return
//     */
//    private OptQuestionList<BigQuestion> selectedBigQuestionOptListTeam(String askKey, String uid, String[] questionIds) {
//
//        List<BigQuestion> list = selectBigQuestionTeam(askKey, uid, questionIds);
//        if (list == null) {
//            return null;
//        } else {
//            return new OptQuestionList<>(ASK_QUESTIONS_SELECTED, list);
//        }
//
//    }
//
//    /**
//     * 被选中的学生获得问卷
//     *
//     * @param askKey
//     * @param uid
//     * @return
//     */
//    private List<TaskQuestion> selectTaskQuestion(String askKey, String uid, String[] questionIds) {
//        if (interact.selectVerify(askKey, uid)) {
//            return (List<TaskQuestion>) taskQuestionRepository.findAllById(Arrays.asList(questionIds));
//        } else {
//            return null;
//        }
//    }
//
//    /**
//     * 被选中的学生获得问卷
//     *
//     * @param askKey
//     * @param uid
//     * @return
//     */
//    private List<TaskQuestion> selectTaskQuestionTeam(String askKey, String uid, String[] questionIds) {
//        if (interact.selectTeamVerify(askKey, uid)) {
//            return (List<TaskQuestion>) taskQuestionRepository.findAllById(Arrays.asList(questionIds));
//        } else {
//            return null;
//        }
//    }
//
//    /**
//     * 被选中的学生获得练习册
//     *
//     * @param askKey
//     * @param uid
//     * @return
//     */
//    private List<BigQuestion> selectBigQuestion(String askKey, String uid, String[] questionIds) {
//        if (interact.selectVerify(askKey, uid)) {
//            return (List<BigQuestion>) bigQuestionRepository.findAllById(Arrays.asList(questionIds));
//        } else {
//            return null;
//        }
//    }
//
//    /**
//     * 被选中的学生获得练习册
//     *
//     * @param askKey
//     * @param uid
//     * @return
//     */
//    private List<BigQuestion> selectBigQuestionTeam(String askKey, String uid, String[] questionIds) {
//        if (interact.selectTeamVerify(askKey, uid)) {
//            return (List<BigQuestion>) bigQuestionRepository.findAllById(Arrays.asList(questionIds));
//        } else {
//            return null;
//        }
//    }
//
//    /**
//     * 构建任务返回值
//     *
//     * @param cut
//     * @param questionList
//     * @return
//     */
//    private AskTask buildAskTask(String cut, OptQuestionList<TaskQuestion> questionList) {
//        if (questionList != null) {
//            return new AskTask<>(cut, questionList.getList(), questionList.getSelected());
//        } else {
//            return null;
//        }
//    }
//
//    /**
//     * 构建任务返回值
//     *
//     * @param cut
//     * @param questionList
//     * @return
//     */
//    private AskBook buildAskBook(String cut, OptQuestionList<BigQuestion> questionList) {
//        if (questionList != null) {
//            return new AskBook<>(cut, questionList.getList(), questionList.getSelected());
//        } else {
//            return null;
//        }
//    }
}
