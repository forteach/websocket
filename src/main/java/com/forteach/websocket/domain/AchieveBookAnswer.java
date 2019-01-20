package com.forteach.websocket.domain;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/20  18:01
 */
@Data
public class AchieveBookAnswer {

    private String model = "answerBook";

    private List<Students> students;

    public AchieveBookAnswer(List<Students> students) {
        this.students = students;
    }
}
