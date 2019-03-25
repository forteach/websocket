package com.forteach.websocket.service.teacher.push.repeat;


import com.forteach.websocket.common.ClassRoomKey;
import org.springframework.stereotype.Service;

/**
 * 题目回答去重过滤
 */
@Service
public class AnswerRepeat extends AbsRepeatPush {

    /**
     * 判断题目是否已经加入推送缓存
     * @param circleId
     * @param stuId
     * @return
     */
    public boolean answerHasJoin(String circleId,String stuId){
        final String key =ClassRoomKey.getJoinTuisongAnswerKey(circleId);
        return hasJoin(key,stuId);
    }

    /**
     * 将课堂学生回答题目Id已推送列表推送过
     * @param circleId
     * @param stuId
     * @return
     */
    public String joinAnswer(String circleId, String stuId){
        final String key =ClassRoomKey.getJoinTuisongAnswerKey(circleId);
       return join(key,stuId);
    }

    /**
     * 清楚课堂学生回答题目Id推送缓存
     * @param circleId
     * @param teacherId
     */
    public void clearAnswer(final String circleId,final String teacherId){
        final String key =ClassRoomKey.getJoinTuisongAnswerKey(circleId);
        clearJoinTuiSong(key,circleId,teacherId);
    }
}
