package com.forteach.websocket.service.Key;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/9 11:03
 */
public class MoreQueKey {

    /**
     * 互动练习册发布
     */
    public static final String CLASSROOM_BOOK_QUESTIONS_LIST = "BookList";

    /**
     * 课堂练习册
     */
    public static final String CLASSROOM_BOOK_NOW = "NowBook";

    /**
     * 加入课堂，已推送过得学生回答
     */
    public static final String ROOM_JOIN_MORE_TS = "RoomJoinMore";

    //刷新清除场景命名问题多题目场景
    public static final String CLASSROOM_CLEAR_TAG_MORE = "More";

    /**
     * 互动方式为练习
     */
    public static final String CLASSROOM_BOOK_QUESTIONS_ID = "Book";

    /**
     * 课堂多题目活动互动前缀
     *
     * @return 题目列表List
     */
    public static String bookTypeQuestionsList(final String typeName, final String circleId) {
        return circleId.concat(CLASSROOM_BOOK_QUESTIONS_LIST).concat(typeName);
    }

    /**
     * 课堂题目当前前缀
     *
     * @return now+课堂Id=map
     */
    public static String questionsBookNowMap(final String typeName, final String circleId) {
        return circleId.concat(CLASSROOM_BOOK_NOW).concat(typeName);
    }

    /**
     * 加入课堂，已推送过的学生题目回答
     * @param circleId
     * @param questionId
     * @param pushType  推送类型  pushQe：提问   pushAw：回答
     * @return
     */
    public static String getJoinTuiSongMoreKey(String circleId,String questionId,String pushType,String questionType){
        return circleId.concat(questionId).concat(ROOM_JOIN_MORE_TS).concat(questionType).concat(pushType);
    }

}
