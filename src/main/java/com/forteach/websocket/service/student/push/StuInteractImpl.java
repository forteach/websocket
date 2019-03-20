package com.forteach.websocket.service.student.push;
import com.alibaba.fastjson.JSON;
import com.forteach.websocket.common.*;
import com.forteach.websocket.domain.BigQuestion;
import com.forteach.websocket.repository.BigQuestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;


/**
 * @Description: 学生推送业务数据处理
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/2  15:12
 */
@Slf4j
@Service
public class StuInteractImpl {

    @Resource
    private HashOperations<String, String, String> hashOperations;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private BigQuestionRepository bigQuestionRepository;

    /**
     * 获得题目内容
     * @param questionId
     * @return
     */
    public BigQuestion getBigQuestion(String questionId){
        String key=BigQueKey.QuestionsNow(questionId);
       return stringRedisTemplate.hasKey(key) ? JSON.parseObject(stringRedisTemplate.opsForValue()
               .get(BigQueKey.QuestionsNow(questionId)),BigQuestion.class) : bigQuestionRepository.findById(questionId).orElse(new BigQuestion());
    }

    /**
     * 获得当前课堂的问题
     *
     * @param questionId 题目Id
     * @return
     */
    private BigQuestion findBigQuestion(final String questionId) {

        return bigQuestionRepository.findById(questionId).get();
    }

    /**
     *获得当前课堂活动的题目ID
     * @param type  课堂活动  提问 任务
     * @param circleId
     * @param Interact 课堂互动方式  选人 抢答
     * @return
     */
    public String getNowQuestId(QuestionType type,String circleId,String Interact) {
        String key= BigQueKey.askTypeQuestionsIdNow(type.name(), circleId, Interact);
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 获得当前题目的交互类型
     * @param circleId
     * @return
     */
    public String getNowQuestInteractive(String circleId) {
        return hashOperations.get(BigQueKey.QuestionsIdNow(circleId), "interactive");
    }

    /**
     * 获得当前题目的参与形式
     * @param circleId
     * @return
     */
    public String getNowQuestCategory(String circleId) {
        return hashOperations.get(BigQueKey.QuestionsIdNow(circleId), "category");
    }

    /**
     * 获得当前开课课堂列表
     * @param circleId
     * @return
     */
    public String getQuestSelectStu(String circleId){
        return hashOperations.get(BigQueKey.QuestionsIdNow(circleId), "selected");
    }
}
