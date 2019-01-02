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

    /**
     * 提交答案所需随机数
     */
    private T bigQuestion;

    /**
     * 参与方式
     */
    private String participate;

    public AskQuestion() {
    }

    public AskQuestion(String cut, T bigQuestion, String participate) {
        this.cut = cut;
        this.bigQuestion = bigQuestion;
        this.participate = participate;
    }


}
