package com.forteach.websocket.domain;

import lombok.Data;

import java.util.List;

/**
 * @Description: 学生举手信息
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/29  11:16
 */
@Data
public class AchieveRaise {

    /**
     * 举手
     */
    private String model = "raise";

    private List<Students> students;


    public AchieveRaise(List<Students> students) {
        this.students = students;
    }
}
