package com.forteach.websocket.service.impl;

import com.alibaba.fastjson.JSON;
import com.forteach.websocket.service.Key.MoreQueKey;
import com.forteach.websocket.common.QuestionType;
import com.forteach.websocket.domain.BigQuestion;
import com.forteach.websocket.repository.BigQuestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * @Description: 学生推送业务数据处理
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/2  15:12
 */
@Slf4j
@Service
public class MoreQuestService {

    @Resource
    private HashOperations<String, String, String> hashOperations;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private BigQuestionRepository bigQuestionRepository;

    /**
     * 获得题目内容
     *
     * @param questionId
     * @return
     */
    public BigQuestion getBigQuestion(String questionId) {
        String key = MoreQueKey.QuestionsNow(questionId);
        return stringRedisTemplate.hasKey(key) ? JSON.parseObject(stringRedisTemplate.opsForValue()
                .get(MoreQueKey.QuestionsNow(questionId)), BigQuestion.class) : bigQuestionRepository
                .findById(questionId)
                .orElse(new BigQuestion());
    }


    /**
     * 获得当前课堂活动的多题目列表
     *
     * @param type     课堂问题活动  练习册 、调查
     * @param circleId
     * @return
     */
    public List<String> getNowMoreQuestId(QuestionType type, String circleId) {
        String key = MoreQueKey.bookTypeQuestionsList(type.name(),circleId);
        return stringRedisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 获得当前开课课堂多题列表，未收到推送标记的学生列表
     * @param type 课堂问题活动  练习册 、调查
     * @param circleId
     * @return
     */
    public String getMoreQuestNoReceiveSelectStu(QuestionType type,String circleId) {
        return hashOperations.get(MoreQueKey.questionsBookNowMap(type.name(),circleId), "selected");
    }

}
