package com.forteach.websocket.service.teacher.push.repeat;

import com.forteach.websocket.service.Key.ClassRoomKey;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public abstract class AbsRepeatPush {

    /**
     * 拉取操作
     */
    public static final String ASK_PUSH = "Push";

    /**
     * 推送操作
     */
    public static final String ASK_PULL = "Pull";

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    public boolean hasJoin(String key, String value) {

        boolean result=stringRedisTemplate.hasKey(key);
        if(result){
            result= stringRedisTemplate.opsForSet().isMember(key,value).booleanValue();
        }
            return !result;

    }


    public String join(String key, String value) {
         stringRedisTemplate.opsForSet().add(key,value);
         stringRedisTemplate.expire(key, 2, TimeUnit.HOURS);
         return value;
    }

   //教师清除题目Id相关的所有学生推送数据
    public void clearJoinTuiSong(String delKey, String tagKey,String teacherId) {
        //删除已发送缓存列表
        if(stringRedisTemplate.hasKey(delKey)){
            stringRedisTemplate.delete(delKey);
        }
        //删除随机数变动用户数据
        stringRedisTemplate.opsForSet().remove(tagKey, teacherId);

    }

    //学生清除题目Id推送数据
    public void clearStuJoinTuiSong(String delKey, String tagKey,String studentId) {
        //删除已发送缓存列表
        stringRedisTemplate.opsForSet().remove(delKey,studentId);

        //删除随机数变动用户数据
       stringRedisTemplate.opsForSet().remove(tagKey, studentId);

    }
}
