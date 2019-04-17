package com.forteach.websocket.service.impl;

import com.forteach.websocket.common.ClassRoomKey;
import com.forteach.websocket.common.QuestionType;
import com.forteach.websocket.service.Key.BigQueKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @Description: 班级加入学生
 * @author: zjw
 * @version: V1.0
 * @date: 2019/1/2  15:12
 */
@Slf4j
@Service
public class ClassStudentService {

    @Resource
    private HashOperations<String, String, String> hashOperations;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获得当前开课课堂教师的编号
     *
     * @param circleId
     * @return
     */
    public String getRoomTeacherId(String circleId) {
        return stringRedisTemplate.opsForValue().get(ClassRoomKey.getRoomTeacherKey(circleId));
    }

    /**
     * 获得当前开课课堂列表
     *
     * @return
     */
    public List<String> getOpenRooms() {
        return stringRedisTemplate.opsForSet().members(ClassRoomKey.OPEN_CLASSROOM)
                .stream().collect(Collectors.toList());
    }

    /**
     * circleId 课堂Id
     * intercat 互动方式 选人 、抢答等
     */
    public String getNowQuestId(String circleId, String intercat) {
        String key = BigQueKey.askTypeQuestionsIdNow(QuestionType.TiWen.name(), circleId, intercat);
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 获得班级加入的学生
     *
     * @param
     * @return
     */
    public List<String> getInteractiveStudents(final String circleId, final String teacherId) {
        return stringRedisTemplate.opsForSet().members(ClassRoomKey.getInteractiveIdQra(circleId))
                //需要过滤掉教师ID
                .stream().filter(id -> !id.equals(teacherId))
                .collect(Collectors.toList());
    }


    /**
     * 获取回答的学生id
     *
     * @param askKey
     * @return
     */
    public List<String> getAnswerStudent(String askKey) {
        return Arrays.asList(askSelected(askKey).split(","));
    }

    /**
     * 获取选择信息
     *
     * @param askKey
     * @return
     */
    private String askSelected(final String askKey) {
        return hashOperations.get(askKey, "selected");
    }

}
