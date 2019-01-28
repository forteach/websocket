package com.forteach.websocket.domain;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/24  12:35
 */
@Data
public class Team {

    private String teamId;

    private List<Students> students;

    public Team(String teamId, List<Students> students) {
        this.teamId = teamId;
        this.students = students;
    }
}
