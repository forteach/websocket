package com.forteach.websocket.service.student.push.repeat;

import com.forteach.websocket.service.Key.SingleQueKey;
import com.forteach.websocket.service.Key.TeachRaiseKey;
import com.forteach.websocket.service.Key.ClassRoomKey;
import com.forteach.websocket.service.teacher.push.repeat.AbsRepeatPush;
import org.springframework.stereotype.Service;

/**
 * 拉取题目举手去重过滤
 */
@Service
public class SingleQueRepeat extends AbsRepeatPush {

    /**
     * 判断题目举手学生是否已经加入推送缓存
     * @param circleId
     * @param questionId
     * @param stuId
     * @return
     */
    public boolean hasJoin(String circleId,String questionId,String stuId){
        final String key = SingleQueKey.getJoinTuiSongSingleKey(circleId,questionId,ASK_PULL);
        return hasJoin(key,stuId);
    }

    /**
     * 将课堂题目举手学生已推送列表推送过
     * @param circleId
     * @param questionId
     * @param stuId
     * @return
     */
    public String join(String circleId,String questionId, String stuId){
        final String key = SingleQueKey.getJoinTuiSongSingleKey(circleId,questionId,ASK_PULL);
        return join(key,stuId);
    }

    /**
     * 清除课堂题目举手学生推送缓存  （学生是接收端，刷新重新接受）
     * @param circleId
     * @param questionId
     * @param stuId
     */
    public void clear(final String circleId,String questionId,final String stuId){
        final String delKey = SingleQueKey.getJoinTuiSongSingleKey(circleId,questionId,ASK_PULL);
        final String tagKey =ClassRoomKey.getOpenClassRandomTag(circleId,stuId, SingleQueKey.CLEAR_TAG_SINGLE);
        clearJoinTuiSong(delKey,tagKey);
    }
}