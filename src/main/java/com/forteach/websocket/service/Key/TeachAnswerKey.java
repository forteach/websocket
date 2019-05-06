package com.forteach.websocket.service.Key;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/9 11:03
 */
public class TeachAnswerKey {

    /**
     * 已回答
     */
    public static final String ASK_CIRCLE_ANSWER_DID = "2";


    //刷新清除场景命名问题回答场景
    public static final String CLEAR_TAG_ANSWER="Answer";

    //批改
    public static final String PIGAI="PiGai";

    //回答列表
    public static final String ANSWER_LIST="AnswerList";


    /**
     * 加入课堂，已推送过得学生回答
     */
    public static final String ROOM_JOIN_ANSW_TS = "RoomJoinAnsw";

    /**
     * 加入课堂，已推送过的学生题目回答
     * @param circleId
     * @param questionId
     * @param pushType  推送类型  pushQe：提问   pushAw：回答
     * @return
     */
    public static String getJoinTuisongAnswerKey(String circleId,String questionId,String pushType,String questionType){
        return circleId.concat(questionId).concat(ROOM_JOIN_ANSW_TS).concat(questionType).concat(pushType);
    }

    /**
     * 课堂当前道题目回答前缀
     * sutId  学生ID
     * questionId 问题ID
     * questionType  题目互动方式  提问、联练习。。。。
     * @return 单个题目ID+前缀+学生编号=题目答案  Hashmap
     */
    public static String answerTypeQuestionsId(final String circleId,String questionId,String questionType) {
        return circleId.concat(questionId).concat(CLEAR_TAG_ANSWER).concat(questionType);
    }

    /**
     * 课堂当前道题目回答批改前缀
     * sutId  学生ID
     * questionId 问题ID
     * questionType  题目互动方式  提问、联练习。。。。
     * @return 单个题目ID+前缀+学生编号=题目答案=Hashmap
     */
    public static String piGaiTypeQuestionsId(final String circleId,String questionId,String questionType) {
        return circleId.concat(questionId).concat(PIGAI).concat(questionType);
    }

    /**
     * 课堂当前道题目回答学生列表前缀
     * sutId  学生ID
     * questionId 问题ID
     * questionType  题目互动方式  提问、联练习。。。。
     * @return 单个题目ID+前缀+学生编号=题目答案=Hashmap
     */
    public static String answerTypeQuestStuList(final String circleId,String questionId,String questionType) {
        //TODO 需要处理为空??
        return circleId.concat(questionId).concat(ANSWER_LIST).concat(questionType);
    }

}
