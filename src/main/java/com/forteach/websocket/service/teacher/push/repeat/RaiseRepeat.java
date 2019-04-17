package com.forteach.websocket.service.teacher.push.repeat;


import com.forteach.websocket.service.Key.BigQueKey;
import com.forteach.websocket.common.ClassRoomKey;
import org.springframework.stereotype.Service;

/**
 * 题目回答去重过滤
 */
@Service
public class RaiseRepeat extends AbsRepeatPush {

    /**
     * 判断题目举手学生是否已经加入推送缓存
     * @param circleId
     * @param questionId
     * @param stuId
     * @return
     */
    public boolean answerHasJoin(String circleId,String questionId,String stuId){
        final String key =ClassRoomKey.getJoinTuisongRaiseKey(circleId,questionId);
        return hasJoin(key,stuId);
    }

    /**
     * 将课堂题目举手学生已推送列表推送过
     * @param circleId
     * @param questionId
     * @param stuId
     * @return
     */
    public String joinAnswer(String circleId,String questionId, String stuId){
        final String key =ClassRoomKey.getJoinTuisongRaiseKey(circleId,questionId);
        return join(key,stuId);
    }

    /**
     * 清楚课堂题目举手学生推送缓存
     * @param circleId
     * @param questionId
     * @param teacherId
     */
    public void clearAnswer(final String circleId,String questionId,final String teacherId){
        final String delKey =ClassRoomKey.getJoinTuisongRaiseKey(circleId,questionId);
        final String tagKey =ClassRoomKey.getOpenClassRandomTag(circleId,teacherId, BigQueKey.CLASSROOM_CLEAR_TAG_RAISE);
        clearJoinTuiSong(delKey,tagKey);
    }
}