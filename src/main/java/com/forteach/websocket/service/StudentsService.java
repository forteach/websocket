package com.forteach.websocket.service;

import com.forteach.websocket.domain.Students;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/29  10:11
 */
public interface StudentsService {
    /**
     * 通过学生id从redis取出学生对象信息
     * @param id
     * @return
     */
    Students findStudentsBrief(final String id);
}
