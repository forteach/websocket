package com.forteach.websocket.web.vo;

import lombok.Data;

import static com.forteach.websocket.web.ws.WsServer.HEARTBEAT_PONG;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/29  9:44
 */
@Data
public class PongVo {

    private String model = HEARTBEAT_PONG;

}
