package com.forteach.websocket.common;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/9 11:03
 */
public class BigQueKey {

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


    /**
     * 课堂互动的hash前缀
     *
     * @return 问题前缀+课堂+问题类型+回答方式
     */
    public static String askTypeQuestionsId(final QuestionType type, BigQuestionGiveVo giveVo) {
        return giveVo.getCircleId().concat(BigQueKey.CLASSROOM_ASK_QUESTIONS_ID).concat(type.name().concat(giveVo.getInteractive()));
    }

    /**
     * 课堂互动的hash前缀
     *
     * @return 当前问题前缀+课堂+问题类型+回答方式
     */
    public static String askTypeQuestionsIdNow(final QuestionType type, BigQuestionGiveVo giveVo) {
        return askTypeQuestionsId(type,giveVo).concat("now");
    }

    /**
     * 课堂互动的hash前缀
     *
     * @return 上一次的问题前缀+课堂+问题类型+回答方式
     */
    public static String askTypeQuestionsIdPrve(final QuestionType type, BigQuestionGiveVo giveVo) {
        return askTypeQuestionsId(type,giveVo).concat("prve");
    }

    /**
     * 课堂互动的hash前缀
     *
     * @return 问题前缀+课堂+问题类型+回答方式+单个题目ID，用于改题目过期判断数据依据
     */
    public static String askTypeQuestionsId(final QuestionType type, BigQuestionGiveVo giveVo,String questionId) {
        return askTypeQuestionsId(type,giveVo).concat(questionId);
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

}
