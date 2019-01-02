package com.forteach.websocket.domain;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/29  11:16
 */
@Data
public class AchieveRaise {

    private String model = "raise";

    private List<Students> students;


    public AchieveRaise(List<Students> students) {
        this.students = students;
    }
}
