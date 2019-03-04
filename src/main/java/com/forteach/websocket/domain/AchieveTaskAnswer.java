package com.forteach.websocket.domain;

import lombok.Data;
import java.util.List;

/**
 * @Description:  学生任务
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/20  14:45
 */
@Data
public class AchieveTaskAnswer {

    private String model = "answerTask";

    private List<Students> students;

    public AchieveTaskAnswer(List<Students> students) {
        this.students = students;
    }
}
