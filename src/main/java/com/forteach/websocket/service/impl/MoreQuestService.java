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
    public boolean filterStu(final String circleId, final String questId, final String stuId) {
        //获得随机数状态,页面刷新会改变随机数状态
        String radonTag = stringRedisTemplate.opsForValue().get(ClassRoomKey.getOpenClassRandomTag(circleId, stuId, SingleQueKey.CLEAR_TAG_SINGLE));
        //随机数改变，过滤已发送过的学生
        if (ClassRoomKey.OPEN_CLASSROOM_RANDOM_TAG_YES.equals(radonTag)) {
            //清除推送学生数据，改变随机值状态也N
            moreQueRepeat.clear(circleId, questId, stuId);
        }
        //推送学生信息
        return getMoreStudent(circleId, questId, stuId);
    }

    /**
     * 获取问题已回答的学生id是否已经加入推送列表
     *
     * @param circleId
     * @param questId
     * @param stuId  接受题目学生的ID
     * @return
     */
    public boolean getMoreStudent(String circleId, String questId, String stuId) {
        //判断是否已经加入推送列表
        boolean result= !moreQueRepeat.hasJoin(circleId, questId, stuId);
        if(result){
            //没有加入，就加入推送列表
            moreQueRepeat.join(circleId, questId, stuId);
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
