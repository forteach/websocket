package com.forteach.websocket.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.forteach.websocket.service.Key.ClassRoomKey;
import com.forteach.websocket.service.Key.TeachAnswerKey;
import com.forteach.websocket.service.Key.SingleQueKey;
import com.forteach.websocket.service.teacher.push.repeat.AnswerRepeat;
import com.forteach.websocket.web.vo.DataDatumVo;
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
    public List<String> getAnswerStu(String circleId, String questId, String questionType, String teacherId) {
        //获得随机数状态,页面刷新会改变随机数状态
//        String radonTag = stringRedisTemplate.opsForValue().get(ClassRoomKey.getOpenClassRandomTag(circleId, teacherId, TeachAnswerKey.CLEAR_TAG_ANSWER));
        //随机数改变，过滤已发送过回答信息的学生
        String key=ClassRoomKey.getOpenClassRandomTag(circleId,TeachAnswerKey.CLEAR_TAG_ANSWER);
        Boolean bl= stringRedisTemplate.opsForSet().isMember(key,teacherId);
        //随机数改变，过滤已发送过的学生
        if (bl.booleanValue()) {
            //清除推送学生数据，改变随机值状态也N
            answerRepeat.clearAnswer(circleId, questId, teacherId,questionType);
        }
        //推送学生信息
        return getAnswerStudent(circleId, questId, questionType);
    }

    /**
     * 获取问题已回答的学生id
     *
     * @param circleId
     * @param questId
     * @param questionType
     * @return
     */
    public List<String> getAnswerStudent(String circleId, String questId, String questionType) {
        //获得学生回答顺序列表
        return stringRedisTemplate.opsForList().range(TeachAnswerKey.answerTypeQuestStuList(circleId, questId, questionType), 0, -1)
                .stream()
                .filter(id -> answerRepeat.answerHasJoin(circleId, questId, id,questionType))
                .map(id -> answerRepeat.joinAnswer(circleId, questId, id,questionType))
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
        return hashOperations.get(TeachAnswerKey.answerTypeQuestionsId(circleId, questId, typeName), examineeId);
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
        return hashOperations.get(TeachAnswerKey.piGaiTypeQuestionsId(circleId, questId, typeName), examineeId);
    }



    /**
     * 课堂当前道题目回答前缀
     * sutId  学生ID
     * questionId 问题ID
     * questionType  题目互动方式  提问、联练习。。。。
     * @return 单个题目ID+前缀+学生编号=题目答案  Hashmap
     */
    private String answerFileTypeQuestionsId(final String circleId, final String questionId, final String questionType) {
        return circleId.concat(questionId).concat("AnswerFile").concat(questionType);
    }

    /**
     * 查找学生回答的附件信息
     * @param circleId
     * @param questionId
     * @param questionType
     * @param studentId
     * @return
     */
    public List<DataDatumVo> findFileList(final String circleId, final String questionId, final String questionType, final String studentId){
        String key = answerFileTypeQuestionsId(circleId, questionId, questionType);
        return JSONObject.parseArray(hashOperations.get(key, studentId), DataDatumVo.class);
    }
}