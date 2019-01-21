package com.forteach.websocket.repository;

import com.forteach.websocket.domain.BrainstormQuestion;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description: 头脑风暴题对象
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/15  16:18
 */
public interface BrainstormQuestionRepository extends MongoRepository<BrainstormQuestion, String> {
}
