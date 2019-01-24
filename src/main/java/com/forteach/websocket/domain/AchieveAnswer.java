package com.forteach.websocket.domain;

import lombok.Data;

import java.util.List;

/**
 * @Description: 学生答题情况
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/29  14:58
 */
@Data
public class AchieveAnswer {

    private String model = "answer";

    private List<Students> students;


    public AchieveAnswer(List<Students> students) {
        this.students = students;
    }


}
