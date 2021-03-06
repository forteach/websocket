package com.forteach.websocket.service.impl;

import com.forteach.websocket.service.Key.ClassRoomKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.time.Duration;
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
     * 设置当前课堂当前活动主题
     * @param circleId
     */
    public String getInteractionType(String circleId){
        final String key= ClassRoomKey.getInteractionType(circleId);
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 设置当前课堂当前活动主题
     * @param circleId
     */
    public Boolean isInteractionType(String circleId){
        final String key= ClassRoomKey.getInteractionType(circleId);
        return stringRedisTemplate.hasKey(key);
    }
}
