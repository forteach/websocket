package com.forteach.websocket.service.teacher.push.repeat;


import com.forteach.websocket.common.ClassRoomKey;
import org.springframework.stereotype.Service;

/**
 * 加入课堂的学生去重过滤
 */
@Service
public class JoinStuRepeat extends AbsRepeatPush {


    /**
     * 判断题目是否已经加入推送缓存
     * @param circleId
     * @param stuId
     * @return
     */
    public boolean hasJoinStu(String circleId,String stuId){
        final String key =ClassRoomKey.getJoinTuisongStuKey(circleId);
        return hasJoin(key,stuId);
    }

    /**
     * 将课堂学生回答题目Id已推送列表推送过
     * @param circleId
     * @param stuId
     * @return
     */
    public String joinStu(String circleId, String stuId) {
        final String key = ClassRoomKey.getJoinTuisongStuKey(circleId);
        return join(key, stuId);
    }

    /**
     * 清除课堂学生回答题目Id推送缓存
     * @param circleId
     * @param teacherId
     */
    public void clearJoinStu(final String circleId,final String teacherId){
        final String delKey =ClassRoomKey.getJoinTuisongStuKey(circleId);
        final String tagKey =ClassRoomKey.getOpenClassRandomTag(circleId,teacherId);
        clearJoinTuiSong(delKey,tagKey);
    }
}
