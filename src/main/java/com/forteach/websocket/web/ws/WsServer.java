package com.forteach.websocket.web.ws;

import com.alibaba.fastjson.JSON;
import com.forteach.websocket.config.WsContextProvider;
import com.forteach.websocket.service.TokenService;
import com.forteach.websocket.service.WsService;
import com.forteach.websocket.service.impl.WorkerForSubImpl;
import com.forteach.websocket.util.StringUtil;
import com.forteach.websocket.web.vo.PongVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/26  11:04
 */
@Slf4j
@Component
@ServerEndpoint(value = "/interactive/{circle}/{token}/{random}", configurator = WsContextProvider.class)
public class WsServer {

    private final TokenService tokenService;

    public WsServer(TokenService tokenService){
        this.tokenService = tokenService;
    }

    public static final String HEARTBEAT_PONG = "pong";
    private static final AtomicLong ONLINE_COUNT = new AtomicLong(0);
    private static final String HEARTBEAT_PING = "ping";
    @Resource
    private WsService wsSvc;

    @Resource
    private ThreadPoolExecutor subExecutor;

    /**
     * 用户班级
     */
    private String circle;
    /**
     * 用户id
     */
    private String uid;
    /**
     * 加入类型　teacher
     */
    private String type;
    /**
     * 随机数
     */
    private String random;

    /**
     * 接受参数建立连接
     * @param session　用户session
     * @param circle　课堂小组id
     * @param token 用户token
     * @param random 随机数
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("circle") String circle, @PathParam("token") String token, @PathParam("random") String random) {
        if (StringUtil.isNotEmpty(token)){
            //验证token
            tokenService.validate(token);
        }
        uid = tokenService.getUid(token);
        type = tokenService.getType(token);
        //属性赋值
        evaluation(circle, uid, type, random);

        log.info("New session opened , current connections [{}] / session id [{}]" +
                        "{circle} [{}]/{uid} [{}]/{type} [{}]/{random} [{}]", ONLINE_COUNT.incrementAndGet(), session.getId(),
                circle, uid, type, random);

        //用户注册 订阅
        subExecutor.execute(new WorkerForSubImpl(this.circle, this.uid, this.type, this.random, session, this.wsSvc));
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        if (message != null && message.equalsIgnoreCase(HEARTBEAT_PING)) {
            try {
                log.trace(" onMessage Pong: " + ByteBuffer.wrap("health-check".getBytes()));
                session.getBasicRemote().sendText(JSON.toJSONString(new PongVo()));
            } catch (IOException e) {
                log.error("onMessage： ERROR ! [{}]", e.getMessage());
            }
        }

    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        wsSvc.unSubscript(this.uid);
        log.info("onClose: One closed, current connections [{}]", ONLINE_COUNT.decrementAndGet());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("ws : onError message : [{}] : throwable : [{}]", throwable.getMessage(), throwable);
    }

    private void evaluation(String circle, String uid, String type, String random) {
        this.circle = circle;
        this.uid = uid;
        this.type = type;
        this.random = random;
    }

}
