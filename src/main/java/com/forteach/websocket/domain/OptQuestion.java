package com.forteach.websocket.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

/**
 * @Description:  题目信息
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/24  16:11
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OptQuestion extends QuestionExamEntity {

    private String selected;

    public OptQuestion() {
    }

    public OptQuestion(String selected, QuestionExamEntity question) {
        this.selected = selected;
        BeanUtils.copyProperties(question, this);
    }
}
