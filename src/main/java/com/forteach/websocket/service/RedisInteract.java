package com.forteach.websocket.service;

import java.util.List;
import java.util.Set;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/2  15:14
 */
public interface RedisInteract {

    public String getRoomTeacherId(String circleId);

    public List<String> getOpenRooms();

    //获得当前提问题目的ID
    public String getNowQuestId(String circleId);

    //获得当前题目的交互类型和参与形式
    public String getNowQuestType(String circleId, String questId);

    //获得提问题目选中的学生
    public  String getQuestStu(String circleId,String questId);

    //获得加入课堂的学生
    public List<String> getInteractiveStudents(final String circleId, final String teacherId);

    Set<String> getSets(String key);

    String uidType(String uid);

    String uidCircle(String uid);

    String uidRandom(final String uid);

    String askQuestionId(final String askKey);

    String askQuestionCut(final String askKey);

    String askCategoryType(final String askKey);

    String askInteractiveType(final String askKey);

    boolean distinctKeyIsEmpty(final String distinctKey, final String askKey, final String selected);

    boolean answerDistinct(final String distinctKey, final String setKey, final String askKey);

    boolean raiseDistinct(final String distinctKey, final String askKey, int size);

    /**
     * 学生加入信息去重
     * @param distinctKey
     * @param askKey
     * @param size
     * @return
     */
    boolean joinDistinct(final String distinctKey, final String askKey, int size);

    /**
     * 查询没有题的情况
     * @param askKey
     * @return
     */
    boolean untitled(final String askKey);

    /**
     * 获取回答的学生id
     * @param askKey
     * @return
     */
    List<String> getAnswerStudent(String askKey);

    /**
     * 查询是否存在
     * @param redisKey
     * @param examineeId
     * @return
     */
    boolean isMember(final String redisKey, final String examineeId);

    /**
     * 通过 提问key,判断是否是选择
     * @param askKey
     * @param examineeId
     * @return
     */
    Boolean selectVerify(final String askKey, final String examineeId);

    /**
     * 通过 提问key,判断是否是选择
     *
     * @param askKey
     * @return
     */
    Boolean selectTeamVerify(final String askKey, final String examineeId);


}
