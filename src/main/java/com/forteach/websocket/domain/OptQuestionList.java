package com.forteach.websocket.domain;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/16  21:10
 */
@Data
public class OptQuestionList<T> {

    List<T> list;
    private String selected;

    public OptQuestionList() {
    }

    public OptQuestionList(String selected, List<T> list) {
        this.selected = selected;
        this.list = list;
    }
}
