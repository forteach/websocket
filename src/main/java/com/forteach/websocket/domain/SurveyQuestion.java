package com.forteach.websocket.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/10  11:27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "surveyQuestion")
public class SurveyQuestion extends QuestionExamEntity {
}
