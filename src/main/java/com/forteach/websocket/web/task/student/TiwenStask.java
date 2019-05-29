package com.forteach.websocket.web.task.student;

import com.alibaba.fastjson.JSON;
import com.forteach.websocket.common.QuestionType;
import com.forteach.websocket.domain.ToStudentPush;
import com.forteach.websocket.service.Key.SingleQueKey;
import com.forteach.websocket.service.WsService;
import com.forteach.websocket.service.impl.ClassStudentService;
import com.forteach.websocket.service.student.push.SingleQuestionPush;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @Description:获得需要推送的REDIS数据
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/27  14:42
 */
@Slf4j
@Component
public class TiwenStask {

    @Resource
    private ClassStudentService classStudentService;

    @Resource
    private WsService wsService;

    @Resource
    private SingleQuestionPush singleQuestionPush;

    /**
     * 每隔１秒遍历发送一次在redis 推送的学生相关信息
     *
     */
    @Scheduled(initialDelay = 1000 * 2, fixedDelay = 1000)
    public void refreshStudentInfo() {
        //获得正在开课的课堂ID
        singleQuestionPush.getOpenRooms()
                .stream()
                .filter(Objects::nonNull)
//                .peek(c -> {
//                    if (log.isDebugEnabled()) {
//                        log.debug("推送的学生相关信息 circleId : [{}]", c);
//                    }
//                })
                .forEach(circleid -> pushTiwenStudent(circleid)
                );
    }

    //获得提问题目需要推送的学生

    /**
     * 提问任务推送
     *
     * @param circleId
     */
    private void pushTiwenStudent(final String circleId) {
        if(QuestionType.TiWen.name().equals(classStudentService.getInteractionType(circleId))) {
            try {
                //获得需要推送的题目信息
                final List<ToStudentPush> pushList = singleQuestionPush.singleQuestion(circleId, QuestionType.TiWen);
                if (pushList != null && pushList.size() != 0) {
                    if (log.isInfoEnabled()) {
                        log.info("学生接收提问信息　:　[{}]", JSON.toJSONString(pushList));
                    }
                    //处理推送
                    wsService.processStudent(pushList);
                }
            } catch (Exception e) {
                log.error(" refreshInfo Task error {} {}", e.getMessage(), e);
            }
        }
    }
}
