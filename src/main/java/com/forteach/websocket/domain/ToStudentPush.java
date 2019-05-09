package com.forteach.websocket.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:　需要推送的用户id和实体对应的信息
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/28  15:09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToStudentPush {

    /**
     * 需要推送的用户id,(学生，教师)
     */
    private String uid;

    /**
     * 提问问题(BigQuestion)
     */
    private AskQuestion askQuestion;

    /**
     * 学生获取问卷问题
     */
    private AskSurvey askSurvey;

    /**
     * 学生习题任务
     */
    private AskTask askTask;

    /**
     * 习题册(练习册)
     */
    private AskBook askBook;

    /**
     * 头脑风暴
     */
    private AskBrainstorm askBrainstorm;

    /**
     * 题目活动类型   练习 提问  任务。。。
     */
    private String questionType;

}
