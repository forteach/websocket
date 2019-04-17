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
import java.util.List;
import java.util.stream.Collectors;


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
     *获得当前课堂活动的多题目列表
     * @param type  课堂问题活动  练习册 、调查
     * @param circleId
     * @return
     */
    public List<String> getNowMoreQuestId(QuestionType type,String circleId) {
        String key= BigQueKey.bookTypeQuestionsList(circleId);
        return stringRedisTemplate.opsForList().range(key,0,-1);
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
     * 获得当前开课课堂问题选人列表
     * @param circleId
     * @return
     */
    public String getQuestSelectStu(String circleId){
        return hashOperations.get(BigQueKey.QuestionsIdNow(circleId), "selected");
    }

    /**
     * 获得当前开课课堂未收到推送标记的学生列表
     * @param circleId
     * @return
     */
    public String getQuestNoReceiveSelectStu(String circleId){
        return hashOperations.get(BigQueKey.QuestionsIdNow(circleId), "noRreceiveSelected");
    }

    /**
     * 获得当前开课课堂多题列表，未收到推送标记的学生列表
     * @param circleId
     * @return
     */
    public String getMoreQuestNoReceiveSelectStu(String circleId){
        return hashOperations.get(BigQueKey.questionsBookNow(circleId), "selected");
    }

    /**
     * 获得当前班级的所有学生ID
     * @param circleId
     * @return
     */
    public List<String> getClassStus(String circleId ){
        return  stringRedisTemplate.opsForSet().members(ClassRoomKey.getInteractiveIdQra(circleId)).stream().collect(Collectors.toList());
    }

    /**
     *
     * @param circleId  获得搬家
     * @return
     */
    public String getRoomTeacherId(String circleId) {
        return stringRedisTemplate.opsForValue().get(ClassRoomKey.getRoomTeacherKey(circleId));
    }
}
