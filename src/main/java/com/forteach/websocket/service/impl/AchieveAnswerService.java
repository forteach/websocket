package com.forteach.websocket.service.impl;

import com.forteach.websocket.common.ClassRoomKey;
import com.forteach.websocket.service.Key.BigQueKey;
import com.forteach.websocket.service.teacher.push.repeat.AnswerRepeat;
import com.forteach.websocket.service.teacher.push.repeat.JoinStuRepeat;
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
    private JoinStuRepeat joinStuRepeat;

    @Resource
    private AnswerRepeat answerRepeat;



    /**
     * 获得当前课堂提问的题目ID
     *
     * @param circleId
     * @return
     */
    public String getNowQuestionId(String circleId) {
        return hashOperations.get(BigQueKey.QuestionsIdNow(circleId), "questionId");
    }


    /**
     * 获得班级加入的学生ID
     *
     * @param circleId  课堂编号
     * @param teacherId 教师编号
     * @return
     */
    public List<String> getInteractiveStudents(final String circleId, final String teacherId) {
        //获得随机数状态,页面刷新会改变随机数状态
        String radonTag = stringRedisTemplate.opsForValue().get(ClassRoomKey.getOpenClassRandomTag(circleId, teacherId, BigQueKey.CLASSROOM_CLEAR_TAG_JION));
        //随机数改变，清除已发送学生的缓存信息
        if (ClassRoomKey.OPEN_CLASSROOM_RANDOM_TAG_YES.equals(radonTag)) {
            //清除推送学生数据，改变随机值状态也N，未改变状态
            joinStuRepeat.clearJoinStu(circleId, teacherId);
        }
        //推送过滤已发送过的学生
        return getTuiSongStuId(circleId, teacherId);
    }

    /**
     * 生成推送的学生ID
     *
     * @param circleId
     * @param teacherId
     * @return
     */
    private List<String> getTuiSongStuId(final String circleId, final String teacherId) {
        return stringRedisTemplate.opsForSet()
                .members(ClassRoomKey.getInteractiveIdQra(circleId))
                .stream()
                //需要过滤掉教师ID
                .filter(id -> !id.equals(teacherId))
                .filter(id -> joinStuRepeat.hasJoinStu(circleId, id))
                .map(id -> joinStuRepeat.joinStu(circleId, id))
                .collect(Collectors.toList());
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
        String radonTag = stringRedisTemplate.opsForValue().get(ClassRoomKey.getOpenClassRandomTag(circleId, teacherId, BigQueKey.CLASSROOM_CLEAR_TAG_ANSWER));
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
        return stringRedisTemplate.opsForList().range(BigQueKey.answerTypeQuestStuList(circleId, questId, typeName), 0, -1)
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
        return hashOperations.get(BigQueKey.answerTypeQuestionsId(circleId, questId, typeName), examineeId);
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
        return hashOperations.get(BigQueKey.piGaiTypeQuestionsId(circleId, questId, typeName), examineeId);
    }


}