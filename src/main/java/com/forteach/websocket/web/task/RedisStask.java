package com.forteach.websocket.web.task;

import com.forteach.websocket.domain.ToPush;
import com.forteach.websocket.service.InteractService;
import com.forteach.websocket.service.WsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/27  14:42
 */
@Slf4j
@Component
public class RedisStask {

    @Resource
    private InteractService interactService;

    @Resource
    private WsService wsService;

    /**
     * 每隔１秒遍历发送一次在redis 推送的信息
     */
    @Scheduled(initialDelay = 1000 * 10, fixedDelay = 100)
    public void refreshInfo() {
        try {
            // 获取redis中待推送的数据
            List<ToPush> pushList = interactService.obtain();
            if (pushList != null && pushList.size() != 0) {
                //处理推送
                wsService.process(pushList);
            }
        } catch (Exception e) {
            log.error(" refreshInfo Task error {} {}", e.getMessage(), e);
        }
    }


}
