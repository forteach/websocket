package com.forteach.websocket.service.student.push.repeat;

import com.forteach.websocket.service.Key.SingleQueKey;
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
    public boolean hasJoin(String circleId,String questionId,String stuId,String interactive,String questionType){
        final String key = SingleQueKey.getJoinTuiSongSingleKey(circleId,questionId,interactive,ASK_PULL,questionType);
        boolean result=hasJoin(key,stuId);
        return result;
    }

    /**
     * 将课堂题目举手学生已推送列表推送过
     * @param circleId
     * @param questionId
     * @param stuId
     * @return
     */
    public String join(String circleId,String questionId, String stuId,String interactive,String questionType){
        final String key = SingleQueKey.getJoinTuiSongSingleKey(circleId,questionId,interactive,ASK_PULL,questionType);
        return join(key,stuId);
    }

    /**
     * 清除课堂题目举手学生推送缓存  （学生是接收端，刷新重新接受）
     * @param circleId
     * @param questionId
     * @param interactive  选人、举手、抢答
     */
    public void clear(final String circleId,String questionId,final String interactive,String studentId,String questionType){
        final String delKey = SingleQueKey.getJoinTuiSongSingleKey(circleId,questionId,interactive,ASK_PULL,questionType);
//        final String tagKey =ClassRoomKey.getOpenClassRandomTag(circleId,stuId, SingleQueKey.CLEAR_TAG_SINGLE);
        final String tagKey =ClassRoomKey.getOpenClassRandomTagChange(circleId);
        clearStuJoinTuiSong(delKey,tagKey,studentId);
    }
}