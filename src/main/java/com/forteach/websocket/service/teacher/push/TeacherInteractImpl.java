package com.forteach.websocket.service.teacher.push;

import com.forteach.websocket.common.BigQueKey;
import com.forteach.websocket.common.ClassRoomKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Collection;
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
public class TeacherInteractImpl {

    @Resource
    private HashOperations<String, String, String> hashOperations;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获得当前课堂提问的题目ID
     * @param circleId
     * @return
     */
    public String getNowQuestionId(String circleId){
        return  hashOperations.get(BigQueKey.QuestionsIdNow(circleId),"questionId");
    }


    /**
     * 获得班级加入的学生
     *
     * @param
     * @return
     */
    public List<String> getInteractiveStudents(final String circleId, final String teacherId) {
        return  stringRedisTemplate.opsForSet().members(ClassRoomKey.getInteractiveIdQra(circleId))
                .stream()
                //需要过滤掉教师ID
                .filter(id -> !id.equals(teacherId))
                .collect(Collectors.toList());
    }

    /**
     * 获取问题已回答的学生id
     * @param circleId
     * @param questId
     * @param typeName
     * @return
     */
    public List<String> getAnswerStudent(String circleId,String questId,String typeName) {
        return stringRedisTemplate.opsForList().range(BigQueKey.answerTypeQuestStuList(circleId, questId, typeName),0,-1);
    }

    /**
     * 获得学生题目回答的答案
     * @param circleId
     * @param questId
     * @param typeName
     * @param examineeId
     * @return
     */
    public String getQuestAnswer(String circleId,String questId,String typeName,String examineeId){
       return  hashOperations.get(BigQueKey.answerTypeQuestionsId(circleId,questId,typeName),examineeId);
    }

    public String piGaiResult(String circleId,String questId,String typeName,String examineeId){
        return  hashOperations.get(BigQueKey.piGaiTypeQuestionsId(circleId,questId,typeName),examineeId);
    }

    /**
     * 查看回答标志
     *
     * @param askKey
     * @return
     */
    private String findAnswerFlag(final String askKey) {
        return hashOperations.get(askKey, "answerFlag");
    }

    /**
     * 获得当前开课课堂教师的编号
     * @param circleId
     * @return
     */
    public String getRoomTeacherId(String circleId) {
        return stringRedisTemplate.opsForValue().get(ClassRoomKey.getRoomTeacherKey(circleId));
    }

    /**
     * 获得当前开课课堂列表
     * @return
     */
    public List<String> getOpenRooms() {
        return stringRedisTemplate.opsForSet().members(ClassRoomKey.OPEN_CLASSROOM)
                .stream()
                .collect(Collectors.toList());
    }

}
