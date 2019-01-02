package com.forteach.websocket.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/7  15:38
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CircleAnswer extends Students {


    private String state;

    private AskAnswer askAnswer;

    public CircleAnswer(String state, AskAnswer askAnswer) {
        this.state = state;
        this.askAnswer = askAnswer;
    }


    public CircleAnswer(Students students, String state, AskAnswer askAnswer) {
        BeanUtils.copyProperties(students, this);
        this.state = state;
        this.askAnswer = askAnswer;
    }
}
