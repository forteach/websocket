package com.forteach.websocket.common;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/9 11:03
 */
public class ClassRoomKey {


    /**
     * 课堂SCOKET随机数YEs状态
     */
    public static final String OPEN_CLASSROOM_Random_TAG_YES = "Y";

    /**
     * 课堂SCOKET随机数NO状态
     */
    public static final String OPEN_CLASSROOM_Random_TAG_NO = "N";

    /**
     * 课堂相关信息ID-Redis的编码前缀
     */
    public static final String CLASS_ROOM_QR_CODE_PREFIX = "RoomMember";

    /**
     * 老师创建临时课堂前缀
     */
    public static final String INTERACTIVE_CLASSROOM = "RoomTeacher";

    /**
     * 老师创建临时课堂前缀
     */
    public static final String OPEN_CLASSROOM = "OpenRoom";

    /**
     *临时课堂的随机数前缀
     */
    public static final String OPEN_CLASSROOM_Random = "OpenRoomRandom";

    /**
     * 加入课堂，已推送过得学生
     */
    public static final String ROOM_JOIN_STU_TS = "RoomJoinStu";

    /**
     * 获得用户scoket连接的随机数
     * @param circleId
     * @param uid
     * @return
     */
    public static String getOpenClassRandom(String circleId,String uid){
        return circleId.concat(OPEN_CLASSROOM_Random).concat(uid);
    }

    /**
     * 获得用户scoket连接的随机数
     * @param circleId
     * @param uid
     * @return
     */
    public static String getOpenClassRandomTag(String circleId,String uid){
        return circleId.concat(OPEN_CLASSROOM_Random.concat("tag")).concat(uid);
    }

    /**
     * 课堂所有学生
     * @param circleId
     * @return
     */
    public static String getInteractiveIdQra(String circleId){
        return circleId.concat(ClassRoomKey.CLASS_ROOM_QR_CODE_PREFIX);
    }

    /**
     * 课堂创建信息
     * @param teacherId
     * @return
     */
    public static String getRoomKey(String teacherId){

        return ClassRoomKey.INTERACTIVE_CLASSROOM.concat(teacherId);
    }

    /**
     * 课堂的上课教师
     * @param circleId
     * @return
     */
    public static String getRoomTeacherKey(String circleId){
        return circleId.concat(ClassRoomKey.INTERACTIVE_CLASSROOM);
    }

    //加入课堂，已推送过得学生
    public static String getJoinTuisongStuKey(String circleId){
        return circleId.concat(ClassRoomKey.ROOM_JOIN_STU_TS);
    }

}
