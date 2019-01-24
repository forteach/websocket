package com.forteach.websocket.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

/**
 * @Description: 学生答题情况
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/7  15:38
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CircleAnswer extends Students {


    /**
     * 回答状态
     * １未回答
     * ２已回答
     */
    private String state;

    private Object askAnswer;

    public CircleAnswer(String state, Object askAnswer) {
        this.state = state;
        this.askAnswer = askAnswer;
    }


    public CircleAnswer(Students students, String state, Object askAnswer) {
        BeanUtils.copyProperties(students, this);
        this.state = state;
        this.askAnswer = askAnswer;
    }
}
