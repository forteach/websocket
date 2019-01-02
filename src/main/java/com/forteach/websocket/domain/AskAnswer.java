package com.forteach.websocket.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/11/30  9:27
 */
@Data
@Document(collection = "askAnswer")
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
public class AskAnswer extends BaseEntity {

    /**
     * 学生id
     */
    private String examineeId;

    /**
     * 回答参与方式
     */
    private String interactive;

    /**
     * 答案
     */
    private String answer;
    /**
     * 问题id
     */
    private String questionId;

    /**
     * 答案对错
     */
    private String right;

    /**
     * 答案分数
     */
    private String score;

    /**
     * 答案评价
     */
    private String evaluate;

    /**
     * 课堂id
     */
    private String circleId;

    public AskAnswer() {
    }
}

