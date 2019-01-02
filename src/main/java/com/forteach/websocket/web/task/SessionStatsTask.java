package com.forteach.websocket.web.task;

import com.forteach.websocket.service.WsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicLong;

import static com.forteach.websocket.service.WsService.SESSION_MAP;

/**
 * @Description: 定时清理无效的session
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/27  14:48
 */
@Slf4j
@Component
public class SessionStatsTask {

    @Resource
    private WsService wsSvc;

    @Scheduled(initialDelay = 10 * 1000, fixedDelay = 30 * 60 * 1000)
    public void reportCurrentTime() {
        final AtomicLong clearSum = new AtomicLong(0);
        try {
            SESSION_MAP.forEach((uid, session) -> {
                if (!session.isOpen()) {
                    wsSvc.removeSession(uid);
                    clearSum.incrementAndGet();
                }
            });
            log.info(" Clear not enabled session ; now sessionMap size : {} ,clear failure sum {}", SESSION_MAP.size(), clearSum);
        } catch (Exception e) {
            log.error(" SessionStatsTask {}", e.getMessage());
        }
    }
}
