package com.forteach.websocket.service.student.push.repeat;

import com.forteach.websocket.service.Key.MoreQueKey;
import com.forteach.websocket.service.Key.TeachAnswerKey;
import com.forteach.websocket.service.Key.ClassRoomKey;
import com.forteach.websocket.service.teacher.push.repeat.AbsRepeatPush;
import org.springframework.stereotype.Service;

/**
 * 拉取题目回答去重过滤
 */
@Service
public class MoreQueRepeat extends AbsRepeatPush {

    /**
     * 判断题目是否已经加入推送缓存
     * @param circleId
     * @param questionId
     * @param stuId
     * @return
     */
    public boolean moreQueHasJoin(String circleId,String questionId,String stuId){
        final String key = MoreQueKey.getJoinTuiSongMoreKey(circleId,questionId,ASK_PULL);
        return hasJoin(key,stuId);
    }

    /**
     * 将课堂学生回答题目Id已推送列表推送过
     * @param circleId
     * @param questionId
     * @param stuId
     * @return
     */
    public String joinAnswer(String circleId,String questionId, String stuId){
        final String key = MoreQueKey.getJoinTuiSongMoreKey(circleId,questionId,ASK_PULL);
        return join(key,stuId);
    }

    /**
     * 清除课堂学生回答题目Id推送缓存
     * @param circleId
     * @param questionId
     * @param teacherId
     */
    public void clearAnswer(final String circleId,String questionId,final String teacherId){
        final String delKey = MoreQueKey.getJoinTuiSongMoreKey(circleId,questionId,ASK_PULL);
        final String tagKey =ClassRoomKey.getOpenClassRandomTag(circleId,teacherId, MoreQueKey.CLASSROOM_CLEAR_TAG_MORE);
        clearJoinTuiSong(delKey,tagKey);
    }
}