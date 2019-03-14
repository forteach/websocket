package com.forteach.websocket.service;

import com.forteach.websocket.domain.ToTeacherPush;

import java.util.List;

/**
 * @Description: 获取需要推送的课堂的互动数据
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/28  9:38
 */
public interface InteractService {

    /**
     * 获取redis中加入班级的学生
     *
     * @return
     */
    public List<ToTeacherPush> getClassStudent(String circleId,String teachserId);


}
