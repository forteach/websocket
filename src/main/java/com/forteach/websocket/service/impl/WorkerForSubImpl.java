package com.forteach.websocket.service.impl;

import com.forteach.websocket.service.WsService;
import lombok.extern.slf4j.Slf4j;
import javax.websocket.Session;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description: 开启线程推送
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/3/11 16:14
 */
@Slf4j
public class WorkerForSubImpl implements Runnable {

    private String circle;
    private String uid;
    private String type;
    private String random;
    private Session sessions;
    private WsService wsSvc;

    private WorkerForSubImpl() {
    }

    public WorkerForSubImpl(String circle, String uid, String type, String random, Session sessions, WsService wsSvc) {
        this.circle = circle;
        this.uid = uid;
        this.type = type;
        this.random = random;
        this.sessions = sessions;
        this.wsSvc = wsSvc;
    }

    /**　TODO
     * 注释清除redis 重新注册
     */
    @Override
    public void run() {
        //注册session信息
        wsSvc.registerSession(uid, this.sessions);
        //解除可能遗留的信息
        wsSvc.unSubscript(uid);
        if (log.isDebugEnabled()) {
            log.debug("circle [{}], uid [{}], type [{}], random [{}], this.sessions [{}]", circle, uid, type, random, this.sessions);
        }
        //订阅
        wsSvc.subscript(circle, uid, type, random, this.sessions);
    }

}
