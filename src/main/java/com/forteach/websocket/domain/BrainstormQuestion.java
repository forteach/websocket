package com.forteach.websocket.domain;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Description: 头脑风暴题对象
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/15  16:17
 */
@Document(collection = "brainstormQuestion")
public class BrainstormQuestion extends QuestionExamEntity {
}
