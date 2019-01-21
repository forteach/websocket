package com.forteach.websocket.repository;

import com.forteach.websocket.domain.BigQuestion;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description: 题对象
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/28  11:07
 */
public interface BigQuestionRepository extends MongoRepository<BigQuestion, String> {
}
