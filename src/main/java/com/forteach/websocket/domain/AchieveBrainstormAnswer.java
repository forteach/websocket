package com.forteach.websocket.domain;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/20  14:46
 */
@Data
public class AchieveBrainstormAnswer {

    private String model = "answerBrainstorm";

    private List<Students> students;

    public AchieveBrainstormAnswer(List<Students> students) {
        this.students = students;
    }
}
