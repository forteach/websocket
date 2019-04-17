package com.forteach.websocket.web.task.student;

import com.alibaba.fastjson.JSON;
import com.forteach.websocket.common.QuestionType;
import com.forteach.websocket.domain.ToStudentPush;
import com.forteach.websocket.service.WsService;
import com.forteach.websocket.service.student.push.MoreQuestionPush;
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
public class LianXiStask {


    @Resource
    private WsService wsService;

    @Resource
    private MoreQuestionPush moreQuestionPush;

    /**
     * 每隔１秒遍历发送一次在redis 推送的学生相关信息
     */
    @Scheduled(initialDelay = 1000 * 2, fixedDelay = 10000)
    public void refreshStudentInfo() {
        //获得正在开课的课堂ID
        moreQuestionPush.getOpenRooms()
                .stream()
                .filter(Objects::nonNull)
                .peek(c -> {
                    if (log.isDebugEnabled()) {
                        log.debug("推送的学生相关信息 circleId : [{}]", c);
                    }
                })
                .forEach(circleid -> pushLianXiStudent(circleid)
                );
    }


    /**
     * 练习册推送
     *
     * @param circleid
     */
    private void pushLianXiStudent(final String circleid) {

        try {
            //获得需要推送的题目信息
            final List<ToStudentPush> pushList = moreQuestionPush.moreQuestion(circleid, QuestionType.LianXi);
            if (pushList != null && pushList.size() != 0) {
                if (log.isInfoEnabled()) {
                    log.info("练习信息　:　[{}]", JSON.toJSONString(pushList));
                }
                //处理推送
                wsService.processStudent(pushList);
            }
        } catch (Exception e) {
            log.error(" refreshInfo Task error {} {}", e.getMessage(), e);
        }
    }
}
