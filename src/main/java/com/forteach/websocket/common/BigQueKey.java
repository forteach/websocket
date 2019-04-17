package com.forteach.websocket.common;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/9 11:03
 */
public class BigQueKey {

    /**
     * 互动提问hash前缀(习题库\头脑风暴等。。。)
     */
    public static final String CLASSROOM_ASK_NOW= "now";

    /**
     * 互动提问hash前缀(习题库\头脑风暴等。。。)
     */
    public static final String CLASSROOM_ASK_PRVE= "prve";

    /**
     * 课堂提问答案等前缀
     */
    public static final String EXAMINEE_IS_REPLY_KEY = "askReply";

    /**
     * 课堂问题前缀
     */
    public static final String CLASSROOM_ASK_QUESTIONS_RACE = "askRace";

    /**
     * 互动提问hash前缀(习题库\头脑风暴等。。。)
     */
    public static final String CLASSROOM_ASK_QUESTIONS_ID = "ask";

    //刷新清除场景命名加入学生场景
    public static final String CLASSROOM_CLEAR_TAG_JION="join";
    //刷新清除场景命名问题回答场景
    public static final String CLASSROOM_CLEAR_TAG_ANSWER="answer";
    //刷新清除场景命名问题举手场景
    public static final String CLASSROOM_CLEAR_TAG_RAISE="raise";

    /**
     * 互动练习册发布
     */
    public static final String CLASSROOM_BOOK_QUESTIONS_LIST = "bookList";

    /**
     * 课堂练习册
     */
    public static final String CLASSROOM_BOOK_NOW= "nowBook";

    //缓存当前已发布的题目题干内容，不分课堂。
    public static String QuestionsNow(String questionId) {
        return CLASSROOM_ASK_NOW.concat(questionId);
    }

    /**
     * 课堂题目当前前缀
     *
     * @return now+课堂Id=map
     */
    public static String QuestionsIdNow(String circleId) {
        return CLASSROOM_ASK_NOW.concat(circleId);
    }

    /**
     * 课堂题目当前前缀
     *
     * @return 当前问题前缀+课堂+问题类型+回答方式=ForValue
     */
    public static String askTypeQuestionsIdNow(final String questionType, String circleId, String interactive) {
        return CLASSROOM_ASK_NOW+askTypeQuestionsId(questionType,circleId,interactive);
    }

    /**
     * 课堂互动前缀
     *
     * @return 问题前缀+课堂+问题类型+回答方式  题目列表List
     */
    public static String askTypeQuestionsId(final String questionType,String circleId, String interactive) {
        return circleId.concat(BigQueKey.CLASSROOM_ASK_QUESTIONS_ID).concat(questionType.concat(interactive));
    }

    /**
     * 课堂题目上一题前缀
     *
     * @return 上一次的问题前缀+课堂+问题类型+回答方式=ForValu
     */
    public static String askTypeQuestionsIdPrve(final String questionType, String circleId, String interactive) {
        return CLASSROOM_ASK_PRVE.concat(askTypeQuestionsId(questionType,circleId,interactive));
    }

    /**
     * 课堂所选单道题目前缀
     *
     * @return 问题前缀+课堂+问题类型+回答方式+单个题目ID=ForSet
     */
    public static String askTypeQuestionsId(final String questionType, String circleId, String interactive,String questionId) {
        return askTypeQuestionsId(questionType,circleId,interactive).concat(questionId);
    }

    /**
     * 课堂当前道题目回答前缀
     * sutId  学生ID
     * questionId 问题ID
     * typeName  题目互动方式  提问、联练习。。。。
     * @return 单个题目ID+前缀+学生编号=题目答案  Hashmap
     */
    public static String answerTypeQuestionsId(final String circleId,String questionId,String typeName) {
        return questionId.concat("answer").concat(typeName).concat(circleId);
    }

    /**
     * 课堂当前道题目回答批改前缀
     * sutId  学生ID
     * questionId 问题ID
     * typeName  题目互动方式  提问、联练习。。。。
     * @return 单个题目ID+前缀+学生编号=题目答案=Hashmap
     */
    public static String piGaiTypeQuestionsId(final String circleId,String questionId,String typeName) {
        return questionId.concat("pigai").concat(typeName).concat(circleId);
    }

    /**
     * 课堂当前道题目回答学生列表前缀
     * sutId  学生ID
     * questionId 问题ID
     * typeName  题目互动方式  提问、联练习。。。。
     * @return 单个题目ID+前缀+学生编号=题目答案=Hashmap
     */
    public static String answerTypeQuestStuList(final String circleId,String questionId,String typeName) {
        //TODO 需要处理为空??
        return questionId.concat("answerlist").concat(typeName).concat(circleId);
    }

    /**
     * 课堂题目互动类型前缀
     *
     * @return 问题前缀+课堂+问题类型+回答方式+单个题目ID，用于改题目过期判断数据依据
     */
    public static String askTypeQuestionsIdType(final String circleId, String questionId) {
        return  circleId.concat("asknow").concat(questionId);
    }

    /**
     * 课堂互动的hash前缀
     *
     * @return 问题前缀+课堂+问题类型+回答方式
     */
    public static String askQuestionsId(final QuestionType type, String circleId) {
        return circleId.concat(BigQueKey.CLASSROOM_ASK_QUESTIONS_ID).concat(type.name());
    }

    /**
     * 课堂练习互动前缀
     *
     * @return  题目列表List
     */
    public static String bookTypeQuestionsList(final String circleId) {
        return circleId.concat(CLASSROOM_BOOK_QUESTIONS_LIST);
    }

    /**
     * 课堂题目当前前缀
     *
     * @return now+课堂Id=map
     */
    public static String questionsBookNow(final String circleId) {
        return CLASSROOM_BOOK_NOW.concat(circleId);
    }

}
