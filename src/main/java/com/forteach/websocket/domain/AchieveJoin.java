package com.forteach.websocket.domain;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/29  15:58
 */
@Data
public class AchieveJoin {

    /**
     * 互动学生
     */
    private String model = "interactiveStudents";

    private List<Students> students;


    public AchieveJoin(List<Students> students) {
        this.students = students;
    }


}
