package com.forteach.websocket.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:　需要推送的用户id和实体对应的信息
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/28  15:09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToTeacherPush {

    /**
     * 需要推送的用户id,(教师)
     */
    private String uid;
    /**
     * 学生加入课堂信息
     */
    private AchieveJoin achieveJoin;

    /**
     *　学生举手信息
     */
    private AchieveRaise achieveRaise;

    /**
     *　学生回答信息(BigQuestion)
     */
    private AchieveAnswer achieveAnswer;

    /**
     *　实时学生问卷答案
     */
    private AchieveSurveyAnswer achieveSurveyAnswer;

    /**
     *　头脑风暴答案
     */
    private AchieveBrainstormAnswer achieveBrainstormAnswer;

    /**
     *　任务答案
     */
    private AchieveTaskAnswer achieveTaskAnswer;

    /**
     * 习题答案
     */
    private AchieveBookAnswer achieveBookAnswer;



}
