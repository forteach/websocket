package com.forteach.websocket.web.task.student;

import com.alibaba.fastjson.JSON;
import com.forteach.websocket.domain.ToStudentPush;
import com.forteach.websocket.service.RedisInteract;
import com.forteach.websocket.service.WsService;
import com.forteach.websocket.service.student.push.TiWenPush;
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
    private RedisInteract interact;

    @Resource
    private WsService wsService;

    @Resource
    private TiWenPush tiWenPush;

    /**
     * 每隔１秒遍历发送一次在redis 推送的学生相关信息
     * TODO 会推送多条，需要进行判断去重使用 redis
     */
    @Scheduled(initialDelay = 1000 * 2, fixedDelay = 2000)
    public void refreshStudentInfo() {
        //获得正在开课的课堂ID
        interact.getOpenRooms()
                .stream()
                .filter(Objects::nonNull)
                .peek(c -> {
                    if (log.isDebugEnabled()) {
                        log.debug("推送的学生相关信息 circleId : [{}]", c);
                    }
                })
                .forEach(circleid -> pushTiwenStudent(circleid)
                );
    }

    //获得提问题目需要推送的学生

    /**
     * 提问任务推送
     *
     * @param circleid
     */
    private void pushTiwenStudent(final String circleid) {

        try {
            //获得需要推送的题目信息
            final List<ToStudentPush> pushList = tiWenPush.tiWenStudent(circleid);
            if (pushList != null && pushList.size() != 0) {
                if (log.isInfoEnabled()) {
                    log.info("提问信息　:　[{}]", JSON.toJSONString(pushList));
                }
                //处理推送
                wsService.processStudent(pushList);
            }
        } catch (Exception e) {
            log.error(" refreshInfo Task error {} {}", e.getMessage(), e);
        }
    }
}
