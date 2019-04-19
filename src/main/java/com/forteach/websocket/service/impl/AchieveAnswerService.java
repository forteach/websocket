package com.forteach.websocket.service.impl;

import com.forteach.websocket.service.Key.ClassRoomKey;
import com.forteach.websocket.service.Key.AchieveAnswerKey;
import com.forteach.websocket.service.Key.SingleQueKey;
import com.forteach.websocket.service.teacher.push.repeat.AnswerRepeat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 学生推送业务数据处理
 * @author: zjw
 * @version: V1.0
 * @date: 2019/1/2  15:12
 */
@Slf4j
@Service
public class AchieveAnswerService {

    @Resource
    private HashOperations<String, String, String> hashOperations;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private AnswerRepeat answerRepeat;



    /**
     * 获得当前课堂提问的题目ID
     *
     * @param circleId
     * @return
     */
    public String getNowQuestionId(String circleId) {
        return hashOperations.get(SingleQueKey.questionsIdNow(circleId), "questionId");
    }

    /**
     * 获得班级加入的学生ID
     *
     * @param circleId  课堂编号
     * @param teacherId 教师编号
     * @return
     */
    public List<String> getAnswerStu(String circleId, String questId, String typeName, String teacherId) {
        //获得随机数状态,页面刷新会改变随机数状态
        String radonTag = stringRedisTemplate.opsForValue().get(ClassRoomKey.getOpenClassRandomTag(circleId, teacherId, AchieveAnswerKey.CLASSROOM_CLEAR_TAG_ANSWER));
        //随机数改变，过滤已发送过的学生
        if (ClassRoomKey.OPEN_CLASSROOM_RANDOM_TAG_YES.equals(radonTag)) {
            //清除推送学生数据，改变随机值状态也N
            answerRepeat.clearAnswer(circleId, questId, teacherId);
        }
        //推送学生信息
        return getAnswerStudent(circleId, questId, typeName);
    }

    /**
     * 获取问题已回答的学生id
     *
     * @param circleId
     * @param questId
     * @param typeName
     * @return
     */
    public List<String> getAnswerStudent(String circleId, String questId, String typeName) {
        //获得学生回答顺序列表
        return stringRedisTemplate.opsForList().range(AchieveAnswerKey.answerTypeQuestStuList(circleId, questId, typeName), 0, -1)
                .stream()
                .filter(id -> answerRepeat.answerHasJoin(circleId, questId, id))
                .map(id -> answerRepeat.joinAnswer(circleId, questId, id))
                .collect(Collectors.toList());
    }

    /**
     * 获得学生题目回答的答案
     *
     * @param circleId
     * @param questId
     * @param typeName
     * @param examineeId
     * @return
     */
    public String getQuestAnswer(String circleId, String questId, String typeName, String examineeId) {
        return hashOperations.get(AchieveAnswerKey.answerTypeQuestionsId(circleId, questId, typeName), examineeId);
    }

    /**
     * 获得自动批改结果
     *
     * @param circleId
     * @param questId
     * @param typeName
     * @param examineeId
     * @return
     */
    public String piGaiResult(String circleId, String questId, String typeName, String examineeId) {
        return hashOperations.get(AchieveAnswerKey.piGaiTypeQuestionsId(circleId, questId, typeName), examineeId);
    }


}