package com.forteach.websocket.service;

import com.forteach.websocket.domain.ToStudentPush;
import com.forteach.websocket.domain.ToTeacherPush;
import javax.websocket.Session;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/26  16:07
 */
public interface WsService {

    /**
     * 保存session集合
     */
    ConcurrentMap<String, Session> SESSION_MAP = new ConcurrentHashMap<>();

    /**
     * 订阅
     *
     * @param circle
     * @param uid
     * @param type
     * @param random
     * @param sessions
     * @return
     */
    boolean subscript(String circle, String uid, String type, String random, Session sessions);

    /**
     * 取消订阅
     *
     * @return
     */
    boolean unSubscript(String uid);

    /**
     * 通过uid拿到用户对应的session，此部分的实现只能在同一个WebSocket Container内部实现
     * 不能做分布式的实现，所以要求在配置Nginx时使用ip hash 方式或者是sticky
     *
     * @param uid 用户标识id
     * @return 此用户对应的session对象
     */
    Session getSession(String uid);


    /**
     * 移除指定用户的session信息
     *
     * @param uid 指定的用户id
     * @return
     */
    boolean removeSession(String uid);


    /**
     * 用户注册session
     *
     * @param uid     用户id
     * @param session 注册的 web socket的session
     * @return
     */
    boolean registerSession(String uid, Session session);


    /**
     * 处理
     *
     * @param list
     * @return
     */
    void processStudent(List<ToStudentPush> list);

    /**
     * 处理
     *
     * @param list
     * @return
     */
    void processTeacher(List<ToTeacherPush> list);

}
