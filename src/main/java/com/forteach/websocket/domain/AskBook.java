package com.forteach.websocket.domain;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/20  17:53
 */
@Data
public class AskBook<T> {

    private String model = "bookQuestion";

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
    private List<T> bookQuestions;

    public AskBook() {
    }

    public AskBook(String cut, List<T> bookQuestions, String selected) {
        this.cut = cut;
        this.bookQuestions = bookQuestions;
        this.selected = selected;
    }

}
