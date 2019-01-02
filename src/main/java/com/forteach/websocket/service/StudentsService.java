package com.forteach.websocket.service;

import com.forteach.websocket.domain.Students;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/29  10:11
 */
public interface StudentsService {
    Students findStudentsBrief(final String id);
}
