package com.forteach.websocket.service.impl;

import com.forteach.websocket.domain.Students;
import com.forteach.websocket.service.Key.ClassStudentKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 * @Description:
 * @author: zjw
 * @version: V1.0
 * @date: 2018/12/29  10:10
 */
@Slf4j
@Service
public class StudentsService {


    @Resource
    private HashOperations<String, String, String> hashOperations;


    public Students findStudentsBrief(final String id) {
        return Students.builder().id(id).name(findStudentsName(id)).portrait(findStudentsPortrait(id)).build();
    }

    /**
     * 根据用户id 从redis 取出名字信息
     *
     * @param id
     * @return
     */
    private String findStudentsName(final String id) {
        return hashOperations.get(ClassStudentKey.STUDENT_ADO.concat(id), "name");
    }

    /**
     * 从redis 取出头像信息
     *
     * @param id
     * @return
     */
    private String findStudentsPortrait(final String id) {
        return hashOperations.get(ClassStudentKey.STUDENT_ADO.concat(id), "portrait");
    }
}
