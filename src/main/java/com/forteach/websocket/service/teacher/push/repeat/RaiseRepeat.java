package com.forteach.websocket.service.teacher.push.repeat;


import com.forteach.websocket.common.BigQueKey;
import com.forteach.websocket.common.ClassRoomKey;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 题目回答去重过滤
 */
@Service
public class RaiseRepeat extends AbsRepeatPush {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //判断题目举手学生是否已经加入推送缓存
    public boolean answerHasJoin(String circleId,String questionId,String stuId){
        final String key =ClassRoomKey.getJoinTuisongRaiseKey(circleId,questionId);
        return hasJoin(key,stuId);
    }

    //将课堂题目举手学生已推送列表推送过
    public String joinAnswer(String circleId,String questionId, String stuId){
        final String key =ClassRoomKey.getJoinTuisongRaiseKey(circleId,questionId);
       return join(key,stuId);
    }

    //清楚课堂题目举手学生推送缓存
    public void clearAnswer(final String circleId,String questionId,final String teacherId){
        final String delKey =ClassRoomKey.getJoinTuisongRaiseKey(circleId,questionId);
        final String tagKey =ClassRoomKey.getOpenClassRandomTag(circleId,teacherId, BigQueKey.CLASSROOM_CLEAR_TAG_RAISE);
        clearJoinTuiSong(delKey,tagKey);
    }
}
