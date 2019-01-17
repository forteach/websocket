package com.forteach.websocket.domain;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/15  16:00
 */
@Data
public class AskSurvey<T> {

    private String model = "surveyQuestion";

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
    private List<T> surveyQuestions;

    public AskSurvey() {
    }

    public AskSurvey(String cut, List<T> surveyQuestions, String selected) {
        this.cut = cut;
        this.surveyQuestions = surveyQuestions;
        this.selected = selected;
    }
}
