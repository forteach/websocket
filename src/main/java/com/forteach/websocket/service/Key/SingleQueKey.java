package com.forteach.websocket.service.Key;

/**
 * @Description:
 * @author: zjw
 * @version: V1.0
 * @date: 2018/11/9 11:03
 */
public class SingleQueKey {

    /**
     * 提问交互举手
     */
    public static final String ASK_INTERACTIVE_RAISE = "raise";

    /**
     * 提问 选中
     */
    public static final String ASK_QUESTIONS_SELECTED = "1";

    /**
     * 互动提问hash前缀(习题库\头脑风暴等。。。)
     */
    public static final String CLASSROOM_ASK_NOW = "now";

    /**
     * 互动提问hash前缀(习题库\头脑风暴等。。。)
     */
    public static final String CLASSROOM_ASK_PRVE = "prve";

    /**
     * 互动提问hash前缀(习题库\头脑风暴等。。。)
     */
    public static final String CLASSROOM_ASK_QUESTIONS_ID = "ask";

    /**
     * 加入课堂，已推送过得学生回答
     */
    public static final String ROOM_JOIN_SINGLE_TS = "RoomJoinSingle";

    //刷新清除场景命名问题单题目场景
    public static final String CLEAR_TAG_SINGLE = "single";

    /**
     * 加入课堂，已推送过的学生题目回答
     * @param circleId
     * @param questionId
     * @param pushType  推送类型  pushQe：提问   pushAw：回答
     * @return
     */
    public static String getJoinTuiSongSingleKey(String circleId,String questionId,String pushType){
        return pushType.concat(circleId.concat(questionId).concat(ROOM_JOIN_SINGLE_TS));
    }

    //缓存当前已发布的题目题干内容，不分课堂。
    public static String questionsNow(String questionId) {
        return CLASSROOM_ASK_NOW.concat(questionId);
    }
    /**
     * 课堂题目当前前缀
     *
     * @return now+课堂Id=map
     */
    public static String questionsIdNow(String circleId) {
        return CLASSROOM_ASK_NOW.concat(circleId);
    }

    /**
     * 课堂题目当前前缀
     *
     * @return 当前问题前缀+课堂+问题类型+回答方式=ForValue
     */
    public static String askTypeQuestionsIdNow(final String questionType, String circleId, String interactive) {
        return CLASSROOM_ASK_NOW + askTypeQuestionsId(questionType, circleId, interactive);
    }

    /**
     * 课堂互动前缀
     *
     * @return 问题前缀+课堂+问题类型+回答方式  题目列表List
     */
    public static String askTypeQuestionsId(final String questionType, String circleId, String interactive) {
        return circleId.concat(SingleQueKey.CLASSROOM_ASK_QUESTIONS_ID).concat(questionType.concat(interactive));
    }

    /**
     * 课堂所选单道题目前缀
     *
     * @return 问题前缀+课堂+问题类型+回答方式+单个题目ID=ForSet
     */
    public static String askTypeQuestionsId(final String questionType, String circleId, String interactive, String questionId) {
        return askTypeQuestionsId(questionType, circleId, interactive).concat(questionId);
    }

    /**
     * 课堂当前道题目回答学生列表前缀
     * sutId  学生ID
     * questionId 问题ID
     * typeName  题目互动方式  提问、联练习。。。。
     *
     * @return 单个题目ID+前缀+学生编号=题目答案=Hashmap
     */
    public static String answerTypeQuestStuList(final String circleId, String questionId, String typeName) {
        //TODO 需要处理为空??
        return questionId.concat("answerlist").concat(typeName).concat(circleId);
    }

}
