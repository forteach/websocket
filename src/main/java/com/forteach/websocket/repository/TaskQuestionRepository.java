package com.forteach.websocket.repository;

import com.forteach.websocket.domain.TaskQuestion;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @Description: 任务题对象
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/15  16:19
 */
public interface TaskQuestionRepository extends MongoRepository<TaskQuestion, String> {
}
