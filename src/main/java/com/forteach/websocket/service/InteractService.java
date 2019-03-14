package com.forteach.websocket.service;

import com.forteach.websocket.domain.ToStudentPush;
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
     * 获取redis中待推送的教师数据
     *
     * @return
     */
    public List<ToTeacherPush> obtainTeacher(String circleId);
    /**
     * 获取redis中待推送的学生数据
     *
     * @return
     */
   // public List<ToStudentPush> obtainStudent(String circleId,String teachId);

    /**
     * 推送选中学生提问的信息
     * @param circleId
     * @return
     */
    public List<ToStudentPush> tiWenStudent(String circleId);
}
