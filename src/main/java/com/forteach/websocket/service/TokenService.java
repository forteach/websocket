package com.forteach.websocket.service;

import com.auth0.jwt.JWTVerifier;
import org.springframework.http.server.ServerHttpRequest;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-2-20 17:26
 * @version: 1.0
 * @description:
 */
public interface TokenService {
    /**
     * 获取登录的用户信息是微信端的学生信息还是教师信息
     * @param request
     * @return
     */
    String getUid(ServerHttpRequest request);
//    String g
    /**
     * 获取JWT验证
     * @param userId
     * @return
     */
    JWTVerifier verifier(String userId);

    /**
     * 查询学生信息
     * @param token
     * @return
     */
    String getStudentId(String token);

    /**
     * 通过token 判断用户类型
     * @param token
     * @return
     */
    String getType(String token);
}
