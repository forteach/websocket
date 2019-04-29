package com.forteach.websocket.service.impl;

import com.alibaba.fastjson.JSON;
import com.forteach.websocket.service.Key.ClassRoomKey;
import com.forteach.websocket.common.QuestionType;
import com.forteach.websocket.domain.BigQuestion;
import com.forteach.websocket.repository.BigQuestionRepository;
import com.forteach.websocket.service.Key.SingleQueKey;
import com.forteach.websocket.service.Key.TeachAnswerKey;
import com.forteach.websocket.service.student.push.repeat.SingleQueRepeat;
import com.forteach.websocket.service.teacher.push.repeat.AnswerRepeat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @Description: 学生推送单题目业务数据处理
 * @author: zjw
 * @version: V1.0
 * @date: 2019/1/2  15:12
 */
@Slf4j
@Service
public class SingleQuestService {

    @Resource
    private HashOperations<String, String, String> hashOperations;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private BigQuestionRepository bigQuestionRepository;

    @Resource
    private SingleQueRepeat singleQueRepeat;

    /**
     * 获得题目内容
     * @param questionId
     * @return
     */
    public BigQuestion getBigQuestion(String questionId){
        String key= SingleQueKey.questionsNow(questionId);
       return stringRedisTemplate.hasKey(key) ? JSON.parseObject(stringRedisTemplate.opsForValue()
               .get(SingleQueKey.questionsNow(questionId)),BigQuestion.class) : bigQuestionRepository.findById(questionId).orElse(new BigQuestion());
    }


    /**
     *获得当前课堂活动的题目ID
     * @param type  课堂活动  提问 任务
     * @param circleId
     * @param Interact 课堂互动方式  选人 抢答
     * @return
     */
    public String getNowQuestId(QuestionType type,String circleId,String Interact) {
        String key= SingleQueKey.askTypeQuestionsIdNow(type.name(), circleId, Interact);
        return stringRedisTemplate.opsForValue().get(key);
    }


    /**
     * 获得当前题目的交互类型
     * @param circleId
     * @return
     */
    public String getNowQuestInteractive(String circleId) {
        return hashOperations.get(SingleQueKey.questionsIdNow(circleId), "interactive");
    }

    /**
     * 获得当前题目的参与形式
     * @param circleId
     * @return
     */
    public String getNowQuestCategory(String circleId) {
        return hashOperations.get(SingleQueKey.questionsIdNow(circleId), "category");
    }


    /**
     * 获得当前开课课堂未收到推送标记的学生列表
     * @param circleId
     * @return
     */
    public String getQuestNoReceiveSelectStu(String circleId){
        return hashOperations.get(SingleQueKey.questionsIdNow(circleId), "noRreceiveSelected");
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

    /**
     * 获得已经接受推送的学生ID
     *
     * @param circleId  课堂编号
     * @param teacherId 教师编号
     * @return
     */
    public String getSingleStu(final String circleId, final String questId, final String stuId, final String teacherId) {
        //获得随机数状态,页面刷新会改变随机数状态
        String radonTag = stringRedisTemplate.opsForValue().get(ClassRoomKey.getOpenClassRandomTag(circleId, teacherId, SingleQueKey.CLEAR_TAG_SINGLE));
        //随机数改变，过滤已发送过的学生
        if (ClassRoomKey.OPEN_CLASSROOM_RANDOM_TAG_YES.equals(radonTag)) {
            //清除推送学生数据，改变随机值状态也N
            singleQueRepeat.clear(circleId, questId, teacherId);
        }
        //推送学生信息
        return getSingleStudent(circleId, questId, stuId);
    }
    /**
     * 获取问题已回答的学生id
     *
     * @param circleId
     * @param questId
     * @param stuId  接受题目学生的ID
     * @return
     */
    public String getSingleStudent(String circleId, String questId, String stuId) {
        //获得学生回答顺序列表
        return Arrays.asList(stuId)
                .stream()
                .filter(id -> singleQueRepeat.hasJoin(circleId, questId, id))
                .map(id -> singleQueRepeat.join(circleId, questId, id))
                .collect(Collectors.joining());
    }

}
