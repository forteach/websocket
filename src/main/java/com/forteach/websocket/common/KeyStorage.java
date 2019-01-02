package com.forteach.websocket.common;

import static com.forteach.websocket.common.Dic.CLASSROOM_ASK_QUESTIONS_ID;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/27  11:02
 */
public class KeyStorage {

    public static final String INTERACTION_UID_SET_PREFIX = "c.f.w.c.actionUidSet";
    public static final String STUDENT_ADO = "studentsData$";
    private static final String INTERACTION_PROPERTY_PREFIX = "c.f.w.c.actionProperty";
    private static final String CLASSROOM_ASK_QUESTIONS_DISTINCT = "distinctAsk";
    private static final String ASK_RAISE_HAND = "askRaiseHand";
    private static final String RAISE_HAND_STUDENT_DISTINCT = "distinctRaiseHand";
    private static final String RAISE_HAND_STUDENT_JOIN_DISTINCT = "distinctJoinHand";
    private static final String EXAMINEE_IS_REPLY_KEY = "askExamineeIsReply";
    private static final String ANSW_HAND_STUDENT_DISTINCT = "distinctAnswHand";
    private static final String INTERACTIVE_CLASSROOM_STUDENTS = "ICStudents";

    public static String actionPropertyKey(String uid) {
        return INTERACTION_PROPERTY_PREFIX.concat(uid);
    }

    /**
     * 获取redis 提问 key
     *
     * @return
     */
    public static String getAskKey(String circle) {
        return CLASSROOM_ASK_QUESTIONS_ID.concat(circle);
    }

    public static String askQuDistinctKey(String circleId, String examineeId, String questions, String random) {
        return CLASSROOM_ASK_QUESTIONS_DISTINCT
                .concat(circleId)
                .concat(examineeId)
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

    public static String examineeIsReplyKey(String circle) {
        return EXAMINEE_IS_REPLY_KEY.concat(circle);
    }

    public static String getAnswDistinctKey(String circle, String random) {
        return ANSW_HAND_STUDENT_DISTINCT.concat(circle).concat(random);
    }

}
