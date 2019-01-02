package com.forteach.websocket.service;

import com.forteach.websocket.domain.ToPush;

import java.util.List;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/28  9:38
 */
public interface InteractService {

    /**
     * 获取redis中待推送的数据
     *
     * @return
     */
    List<ToPush> obtain();
}
