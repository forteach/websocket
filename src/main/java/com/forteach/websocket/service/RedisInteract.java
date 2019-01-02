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

    boolean joinDistinct(final String distinctKey, final String askKey, int size);

    boolean untitled(final String askKey);

    List<String> getAnswerStudent(String askKey);

    boolean isMember(final String redisKey, final String examineeId);

    Boolean selectVerify(final String askKey, final String examineeId);


}
