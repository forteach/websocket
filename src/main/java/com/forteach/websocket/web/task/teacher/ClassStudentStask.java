package com.forteach.websocket.web.task.teacher;

import com.forteach.websocket.domain.ToTeacherPush;
import com.forteach.websocket.service.RedisInteract;
import com.forteach.websocket.service.WsService;
import com.forteach.websocket.service.teacher.push.ClassStudentPush;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @Description:推送班级加入的学生
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/27  14:42
 */
@Slf4j
@Component
public class ClassStudentStask {

//    @Resource
//    private InteractService interactService;

    @Resource
    private RedisInteract interact;

    @Resource
    private WsService wsService;

    //课堂加入学生学生推送
    @Resource
    private ClassStudentPush classStudentPush ;

    /**
     * 每隔１秒遍历发送一次在redis 推送的教师相关信息
     */
   // @Scheduled(initialDelay = 1000 * 10, fixedDelay = 500)
    public void refreshTeacherInfo() {
        interact.getOpenRooms()
                .stream()
                .filter(Objects::nonNull)
                .forEach(circleid-> pushClassStudent(circleid,interact.getRoomTeacherId(circleid))
                );
    }

    //推动当前加入课堂的学生信息,推送给老师
    private void pushClassStudent(final String circleid,final String teacherId){
        try {
            // 获取redis中待推送的数据
            List<ToTeacherPush> pushList = classStudentPush.getClassStudent(circleid,teacherId);
            if (pushList != null && pushList.size() != 0) {
                //处理推送
                wsService.processTeacher(pushList);
            }
        } catch (Exception e) {
            log.error(" refreshInfo Task error {} {}", e.getMessage(), e);
        }
    }
}
