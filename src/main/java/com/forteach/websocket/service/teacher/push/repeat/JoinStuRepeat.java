package com.forteach.websocket.service.teacher.push.repeat;


import com.forteach.websocket.common.ClassRoomKey;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 加入课堂的学生去重过滤
 */
@Service
public class JoinStuRepeat extends AbsRepeatPush {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //判断题目是否已经加入推送缓存
    public boolean hasJoinStu(String circleId,String stuId){
        final String key =ClassRoomKey.getJoinTuisongStuKey(circleId);
        return hasJoin(key,stuId);
    }

    //将课堂学生回答题目Id已推送列表推送过
    public String joinStu(String circleId, String stuId){
        final String key =ClassRoomKey.getJoinTuisongStuKey(circleId);
       return join(key,stuId);
    }

    //清楚课堂学生回答题目Id推送缓存
    public void clearJoinStu(final String circleId,final String teacherId){
        final String key =ClassRoomKey.getJoinTuisongStuKey(circleId);
        clearJoinTuiSong(key,teacherId);
    }
}
