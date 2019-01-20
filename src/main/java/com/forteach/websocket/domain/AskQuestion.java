package com.forteach.websocket.domain;

import lombok.Data;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/28  10:06
 */
@Data
public class AskQuestion<T> {

    private String model = "questions";

    /**
     * 提交答案所需cut值
     */
    private String cut;

    private T bigQuestion;

    /**
     * 参与方式
     */
    private String participate;

    /**
     * 小组或个人
     */
    private String category;

    public AskQuestion() {
    }

    public AskQuestion(String cut, T bigQuestion, String participate, String category) {
        this.category = category;
        this.cut = cut;
        this.bigQuestion = bigQuestion;
        this.participate = participate;
    }


}
