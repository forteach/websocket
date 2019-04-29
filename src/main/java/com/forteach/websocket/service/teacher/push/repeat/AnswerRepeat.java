package com.forteach.websocket.service.teacher.push.repeat;

import com.forteach.websocket.service.Key.TeachAnswerKey;
import com.forteach.websocket.service.Key.ClassRoomKey;
import org.springframework.stereotype.Service;

/**
 * 题目回答去重过滤
 */
@Service
public class AnswerRepeat extends AbsRepeatPush {

    /**
     * 判断题目是否已经加入推送缓存
     * @param circleId
     * @param questionId
     * @param stuId
     * @return
     */
    public boolean answerHasJoin(String circleId,String questionId,String stuId){
        final String key = TeachAnswerKey.getJoinTuisongAnswerKey(circleId,questionId,ASK_PUSH);
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
        final String key = TeachAnswerKey.getJoinTuisongAnswerKey(circleId,questionId,ASK_PUSH);
        return join(key,stuId);
    }

    /**
     * 清除课堂学生回答题目Id推送缓存
     * @param circleId
     * @param questionId
     * @param teacherId
     */
    public void clearAnswer(final String circleId,String questionId,final String teacherId){
        final String delKey = TeachAnswerKey.getJoinTuisongAnswerKey(circleId,questionId,ASK_PUSH);
        final String tagKey =ClassRoomKey.getOpenClassRandomTag(circleId,teacherId, TeachAnswerKey.CLEAR_TAG_ANSWER);
        clearJoinTuiSong(delKey,tagKey);
    }
}