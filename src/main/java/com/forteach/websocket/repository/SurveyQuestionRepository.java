package com.forteach.websocket.repository;

import com.forteach.websocket.domain.SurveyQuestion;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/15  16:19
 */
public interface SurveyQuestionRepository extends MongoRepository<SurveyQuestion, String> {
}
