package com.forteach.websocket.domain;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/16  22:47
 */
@Data
public class AskTask<T> {


    private String model = "taskQuestion";

    /**
     * 提交答案所需cut值
     */
    private String cut;

    /**
     * 是否被选中
     */
    private String selected;

    /**
     * 问题集
     */
    private List<T> taskQuestion;

    public AskTask() {
    }

    public AskTask(String cut, List<T> taskQuestion, String selected) {
        this.cut = cut;
        this.taskQuestion = taskQuestion;
        this.selected = selected;
    }


}
