package com.forteach.websocket.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.forteach.websocket.service.TokenService;
import com.forteach.websocket.util.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Service;

import static com.forteach.websocket.common.Dic.TOKEN_STUDENT;
import static com.forteach.websocket.common.Dic.USER_TOKEN;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-2-20 17:27
 * @version: 1.0
 * @description:
 */
@Service
public class TokenServiceImpl implements TokenService {

    @Value("${token.salt}")
    private String salt;

    private final HashOperations<String, String, String> hashOperations;

    public TokenServiceImpl(HashOperations<String, String, String> hashOperations){
        this.hashOperations = hashOperations;
    }


    private String getUid(String token){
        return JWT.decode(token).getAudience().get(0);
    }

    @Override
    public String getType(String token){
        return JWT.decode(token).getAudience().get(1);
    }

    @Override
    public String getUid(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst("token");
        String type = getType(token);
        String uId = getUid(token);
        if (StringUtil.isNotEmpty(type) && TOKEN_STUDENT.equals(type)){
            return hashOperations.get(USER_TOKEN.concat(uId), "studentId");
        }
        return uId;
    }

    @Override
    public JWTVerifier verifier(String userId) {
        return JWT.require(Algorithm.HMAC256(salt.concat(userId))).build();
    }

    @Override
    public String getStudentId(String token) {
        return hashOperations.get(USER_TOKEN.concat(getUid(token)), "studentId");
    }
}
