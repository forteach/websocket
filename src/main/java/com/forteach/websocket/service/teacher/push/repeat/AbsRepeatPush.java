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


    public void clearJoinTuiSong(String delKey, String tagKey) {
        //删除已发送缓存列表
        if(stringRedisTemplate.hasKey(delKey)){
            stringRedisTemplate.delete(delKey);
        }
        //修改随机数标记为N未变动
        stringRedisTemplate.opsForValue().set(tagKey, ClassRoomKey.OPEN_CLASSROOM_Random_TAG_NO);

    }
}
