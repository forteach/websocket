package com.forteach.websocket.domain;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/20  14:45
 */
@Data
public class AchieveSurveyAnswer {

    private String model = "answerSurvey";

    private List<Students> students;

    public AchieveSurveyAnswer(List<Students> students) {
        this.students = students;
    }
}
