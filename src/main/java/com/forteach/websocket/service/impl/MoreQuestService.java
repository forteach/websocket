package com.forteach.websocket.service.impl;

import com.alibaba.fastjson.JSON;
import com.forteach.websocket.service.Key.ClassRoomKey;
import com.forteach.websocket.service.Key.MoreQueKey;
import com.forteach.websocket.common.QuestionType;
import com.forteach.websocket.domain.BigQuestion;
import com.forteach.websocket.repository.BigQuestionRepository;
import com.forteach.websocket.service.Key.SingleQueKey;
import com.forteach.websocket.service.student.push.repeat.MoreQueRepeat;
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

    @Resource
    private MoreQueRepeat moreQueRepeat;

    /**
     * 获得已经接受推送的学生ID
     *
     * @param circleId  课堂编号
     * @param stuId 学生接收端编号
     * @return
     */
    public boolean filterStu(final String circleId, final String bookId, final String stuId,final String questionType) {
        //随机数改变，过滤已发送过的学生
        String key=ClassRoomKey.getOpenClassRandomTagChange(circleId);
        Boolean bl= stringRedisTemplate.opsForSet().isMember(key,stuId);
        //随机数改变，过滤已发送过的学生
        if (bl.booleanValue()) {
            //清除推送学生数据，改变随机值状态也N
            moreQueRepeat.clear(circleId, bookId, stuId,questionType);
        }
        //推送学生信息
        return getMoreStudent(circleId, bookId, stuId,questionType);
    }

    /**
     * 获取问题已回答的学生id是否已经加入推送列表
     *
     * @param circleId
     * @param questId
     * @param stuId  接受题目学生的ID
     * @return
     */
    public boolean getMoreStudent(String circleId, String questId, String stuId,final String questionType) {
        //判断是否已经加入推送列表
        boolean result= moreQueRepeat.hasJoin(circleId, questId, stuId,questionType);
        if(result){
            //没有加入，就加入推送列表
            moreQueRepeat.join(circleId, questId, stuId,questionType);
        }
        return result;
    }

    /**
     * 获得题目内容
     *
     * @param questionId
     * @return
     */
    public BigQuestion getBigQuestion(String questionId) {
        String key = SingleQueKey.questionsNow(questionId);
        return stringRedisTemplate.hasKey(key) ? JSON.parseObject(stringRedisTemplate.opsForValue()
                .get(key), BigQuestion.class) : bigQuestionRepository
                .findById(questionId)
                .orElseGet(BigQuestion::new);
    }


    /**
     * 获得当前课堂活动的多题目列表
     *
     * @param questionType     课堂问题活动  练习册 、调查
     * @param circleId
     * @return
     */
        public List<String> getNowMoreQuestId(String questionType, String circleId) {
        String key = MoreQueKey.bookTypeQuestionsList(questionType,circleId);
        return stringRedisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 获得当前开课课堂多题列表，未收到推送标记的学生列表
     * @param questionType 课堂问题活动  练习册 、调查
     * @param circleId
     * @return
     */
    public String getMoreQuestNoReceiveSelectStu(String questionType,String circleId) {
        return hashOperations.get(MoreQueKey.questionsBookNowMap(questionType,circleId), "selected");
    }

    /**
     * 获得当前开课课堂多题列表的唯一ID
     * @param questionType 课堂问题活动  练习册 、调查
     * @param circleId
     * @return
     */
    public String getMoreQuestBookId(String  questionType,String circleId) {
        return hashOperations.get(MoreQueKey.questionsBookNowMap(questionType,circleId), "questBookId");
    }


}
