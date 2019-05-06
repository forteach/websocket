package com.forteach.websocket.service.Key;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/9 11:03
 */
public class ClassRoomKey {

    /**
     * 当前课堂当前互动名称
     */
    public static final String CLASSROOM_NOW_INTERACT= "NowInteract";

    /**
     * 课堂SCOKET随机数YES状态,改变状态
     */
    public static final String OPEN_CLASSROOM_RANDOM_TAG_YES = "Y";

    /**
     * 课堂SCOKET随机数NO状态，未改变状态
     */
    public static final String OPEN_CLASSROOM_RANDOM_TAG_NO = "N";

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
    public static final String OPEN_CLASSROOM_RANDOM = "OpenRoomRandom";

    /**
     * 加入课堂，已推送过得学生
     */
    public static final String ROOM_JOIN_STU_TS = "RoomJoinStu";



    /**
     * 设置当前活动KEY
     *
     * @return
     */
    public static String getInteractionType(final String circleId) {
        return circleId.concat(CLASSROOM_NOW_INTERACT);
    }


    /**
     * 获得用户scoket连接的随机数
     * @param circleId
     * @param uid
     * @return
     */
    public static String getOpenClassRandom(String circleId,String uid){
        return circleId.concat(OPEN_CLASSROOM_RANDOM).concat(uid);
    }

    /**
     * 获得用户scoket连接的随机数改变状态
     * @param circleId
     * @param uid
     * @param tagType 清空标记的场景类型 加入学生  举手  回答
     * @return
     */
    public static String getOpenClassRandomTag(String circleId,String uid,String tagType){
        return circleId.concat(OPEN_CLASSROOM_RANDOM.concat(tagType).concat("Tag")).concat(uid);
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
     * 课堂的上课教师
     * @param circleId
     * @return
     */
    public static String getRoomTeacherKey(String circleId){
        return circleId.concat(ClassRoomKey.INTERACTIVE_CLASSROOM);
    }

    /**
     * 加入课堂，已推送过得学生
     * @param circleId
     * @return
     */
    public static String getJoinTuisongStuKey(String circleId){
        return circleId.concat(ClassRoomKey.ROOM_JOIN_STU_TS);
    }

}