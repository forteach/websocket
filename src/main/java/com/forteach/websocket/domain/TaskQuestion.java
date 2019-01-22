package com.forteach.websocket.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Description: 任务题对象
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/10  11:16
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "taskQuestion")
public class TaskQuestion extends QuestionExamEntity {
}
