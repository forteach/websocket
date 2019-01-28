package com.forteach.websocket.common;

import com.forteach.websocket.domain.QuestionType;

import static com.forteach.websocket.common.Dic.CLASSROOM_ASK_QUESTIONS_ID;

/**
 * @Description: redis 前缀变量和构造需要的key信息
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/27  11:02
 */
public class KeyStorage {

    /**
     *
     */
    public static final String INTERACTION_UID_SET_PREFIX = "c.f.w.c.actionUidSet";
    /**
     * 学生信息前缀
     */
    public static final String STUDENT_ADO = "studentsData$";
    /**
     *
     */
    private static final String INTERACTION_PROPERTY_PREFIX = "c.f.w.c.actionProperty";
    /**
     * 课堂问题
     */
    private static final String CLASSROOM_ASK_QUESTIONS_DISTINCT = "distinctAsk";
    /**
     *
     */
    private static final String ASK_RAISE_HAND = "askRaiseHand";
    /**
     *
     */
    private static final String RAISE_HAND_STUDENT_DISTINCT = "distinctRaiseHand";
    /**
     *
     */
    private static final String RAISE_HAND_STUDENT_JOIN_DISTINCT = "distinctJoinHand";
    /**
     *
     */
    private static final String EXAMINEE_IS_REPLY_KEY = "askExamineeIsReply";
    /**
     *　举手的学生信息前缀
     */
    private static final String ANSW_HAND_STUDENT_DISTINCT = "distinctAnswHand";
    /**
     * 交互课堂的学生信息前缀
     */
    private static final String INTERACTIVE_CLASSROOM_STUDENTS = "ICStudents";

    /**
     * 课堂小组
     */
    private static final String ASK_GROUP = "askGroup";

    public static String actionPropertyKey(String uid) {
        return INTERACTION_PROPERTY_PREFIX.concat(uid);
    }

    /**
     * 获取redis 提问 key
     * @param circle　加入课堂需要的id
     * @return
     */
    public static String getAskKey(String circle) {
        return CLASSROOM_ASK_QUESTIONS_ID.concat(QuestionType.BigQuestion.name()).concat(circle);
    }

    /**
     * 获取学生 获取big question 的去重key
     *
     * @param circleId
     * @param examineeId
     * @param questions
     * @param random
     * @return
     */
    public static String askQuDistinctKey(String circleId, String examineeId, String questions, String random, QuestionType questionType) {
        return CLASSROOM_ASK_QUESTIONS_DISTINCT
                .concat(circleId)
                .concat(examineeId)
                .concat(questionType.name())
                .concat(questions)
                .concat(random);
    }

    public static String interactiveClassKey(String circle) {
        return INTERACTIVE_CLASSROOM_STUDENTS.concat(circle);
    }

    public static String raiseKey(String circle) {
        return ASK_RAISE_HAND.concat(circle);
    }

    public static String raiseDistinctKey(String circle, String random) {
        return RAISE_HAND_STUDENT_DISTINCT.concat(circle).concat(random);
    }

    public static String joinDistinctKey(String circle, String random) {
        return RAISE_HAND_STUDENT_JOIN_DISTINCT.concat(circle).concat(random);
    }

    public static String examineeIsReplyKey(QuestionType type, String circle) {
        return EXAMINEE_IS_REPLY_KEY.concat(type.name()).concat(circle);
    }

    public static String getAnswDistinctKey(String circle, String random) {
        return ANSW_HAND_STUDENT_DISTINCT.concat(circle).concat(random);
    }

    /**
     * 获取课堂下team
     *
     * @param circleId
     * @return
     */
    public static String groupKey(String circleId) {
        return ASK_GROUP.concat(circleId);
    }


}
