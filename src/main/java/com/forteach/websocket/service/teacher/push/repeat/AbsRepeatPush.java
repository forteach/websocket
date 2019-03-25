package com.forteach.websocket.service.teacher.push.repeat;

import com.forteach.websocket.common.ClassRoomKey;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

public abstract class AbsRepeatPush {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public boolean hasJoin(String key, String value) {
        return (!stringRedisTemplate.opsForSet().isMember(key,value).booleanValue());
    }


    public String join(String key, String value) {
         stringRedisTemplate.opsForSet().add(key,value);
         return value;
    }


    public void clearJoinTuiSong(String key, String circleId, String value) {
        if(stringRedisTemplate.delete(key).booleanValue()){

//            stringRedisTemplate.opsForValue().set(ClassRoomKey.getOpenClassRandomTag(key, value), ClassRoomKey.OPEN_CLASSROOM_RANDOM_TAG_NO);

            stringRedisTemplate.opsForValue().set(ClassRoomKey.getOpenClassRandomTag(circleId, value), ClassRoomKey.OPEN_CLASSROOM_RANDOM_TAG_NO);
        }
    }
}
