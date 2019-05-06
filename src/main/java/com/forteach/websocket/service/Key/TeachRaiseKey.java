package com.forteach.websocket.service.Key;

/**
 * @Description:
 * @author: zjw
 * @version: V1.0
 * @date: 2018/11/9 11:03
 */
public class TeachRaiseKey {

    /**
     * 互动提问hash前缀(习题库\头脑风暴等。。。)
     */
    public static final String CLASSROOM_ASK_QUESTIONS_ID = "Ask";

    //刷新清除场景命名问题举手场景
    public static final String CLASSROOM_CLEAR_TAG_RAISE = "Raise";

    /**
     * 加入课堂，已推送过得学生回答
     */
    public static final String ROOM_JOIN_RAISE_TS = "JoinRaise";

    /**
     * 教师端获得举手推送的集合SET列表
     * @param circleId
     * @param questionId
     * @param pushType   推送：push  拉取：pull
     * @return
     */
    public static String getJoinTuisongRaiseKey(String circleId,String questionId,String pushType){
        return  circleId.concat(questionId).concat(ROOM_JOIN_RAISE_TS).concat(pushType);
    }


    /**
     * 课堂互动前缀
     *
     * @return 问题前缀+课堂+问题类型+回答方式  题目列表List
     */
    public static String askTypeQuestionsId(final String questionType, String circleId, String interactive) {
        return circleId.concat(CLASSROOM_ASK_QUESTIONS_ID).concat(questionType.concat(interactive));
    }

    /**
     * 课堂所选单道题目前缀
     *
     * @return 问题前缀+课堂+问题类型+回答方式+单个题目ID=ForSet
     */
    public static String askTypeQuestionsId(final String questionType, String circleId, String interactive, String questionId) {
        return askTypeQuestionsId(questionType, circleId, interactive).concat(questionId);
    }

}
