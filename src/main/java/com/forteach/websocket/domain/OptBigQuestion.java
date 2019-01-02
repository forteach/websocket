package com.forteach.websocket.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/24  16:11
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OptBigQuestion extends BigQuestion {

    private String selected;

    public OptBigQuestion() {
    }

    public OptBigQuestion(String selected, BigQuestion bigQuestion) {
        this.selected = selected;
        BeanUtils.copyProperties(bigQuestion, this);
    }
}
