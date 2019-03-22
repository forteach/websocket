package com.forteach.websocket.service.teacher.push;

import com.forteach.websocket.domain.AchieveRaise;
import com.forteach.websocket.domain.ToTeacherPush;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

/**
 * @Description:学生回答推送给老师
 * @version: V1.0
 * @date: 2019/1/20  14:29
 */
@Slf4j
@Component
public class AchieveRaisePush {

    @Resource
    private TeacherInteractImpl TeacherInteract;


    /**
     * 将课堂加入的学生回答数据，推送给老师
     * circleId 课堂编号
     *teachseId 接受推送的教师
     * @return
     */
    public ToTeacherPush getAchieveRaise(final String circleId) {
        //获得需要课堂的教师ID
        final String teachseId=TeacherInteract.getRoomTeacherId(circleId);
        //创建回答信息
        return ToTeacherPush.builder()
                .uid(teachseId)
                //学生回答信息(BigQuestion)
                .achieveRaise(achieveRaise(circleId))
                .build();

    }

    /**
     * 主动推送给教师 当前问题的回答情况
     *
     * @return
     */
    public AchieveRaise achieveRaise(final String circleId) {
        //获得回答cut随机值
//        String uRandom = "";
        //获得题目ID
        final String questionId =TeacherInteract.getNowQuestionId(circleId);

        final String questionType=TeacherInteract.getNoQuestionType(circleId);
        if (questionId == null){
            return null;
        }
        //获得学生的回答信息
        return  TeacherInteract.achieveRaise(circleId, questionId, questionType);
    }

}
