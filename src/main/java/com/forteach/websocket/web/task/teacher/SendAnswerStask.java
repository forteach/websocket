package com.forteach.websocket.web.task.teacher;

import com.forteach.websocket.domain.ToTeacherPush;
import com.forteach.websocket.service.WsService;
import com.forteach.websocket.service.teacher.push.TeachAnswerPush;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @Description:推送学生回答问题的答案
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/27  14:42
 */
@Slf4j
@Component
public class SendAnswerStask {

    @Resource
    private WsService wsService;

    //课堂加入学生学生推送
    @Resource
    private TeachAnswerPush achieveAnswerPush ;

    /**
     * 每隔１秒遍历发送一次在redis
     */
    @Scheduled(initialDelay = 1000 * 10, fixedDelay = 1000)
    public void refreshTeacherInfo() {
        achieveAnswerPush.getOpenRooms()
                .stream()
                .filter(Objects::nonNull)
                .peek(c -> {
                    if (log.isDebugEnabled()) {
                        log.debug("发送给老师课堂 : circleId : [{}]", c);
                    }
                })
                .forEach(circleId -> pushClassStudent(circleId)
                );
    }

    /**
     * 推动当前加入课堂的学生信息,推送给老师
     *
     * @param circleid
     */
    private void pushClassStudent(final String circleid) {
        try {
            // 获取redis中待推送的数据
            List<ToTeacherPush> pushList = achieveAnswerPush.getAchieveAnswer(circleid);
            if (pushList != null && pushList.size() > 0) {
                //处理推送
                wsService.processTeacher(pushList);
            }
        } catch (Exception e) {
            log.error(" refreshInfo Task error {} {}", e.getMessage(), e);
        }
    }
}
