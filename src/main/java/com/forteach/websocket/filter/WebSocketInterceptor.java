package com.forteach.websocket.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.forteach.websocket.service.TokenService;
import com.forteach.websocket.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import static com.forteach.websocket.common.Dic.TOKEN_STUDENT;
import static com.forteach.websocket.common.Dic.TOKEN_TEACHER;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-2-21 09:57
 * @version: 1.0
 * @description:
 */
@Slf4j
public class WebSocketInterceptor implements HandshakeInterceptor {

    private final TokenService tokenService;

    private WebSocketInterceptor(TokenService tokenService){
        this.tokenService = tokenService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
//        String token = request.getHeaders().getFirst("token");
        request.getMethod();
        String token = "";
        if (StringUtil.isNotEmpty(token)) {
            log.info("token : {} 用户建立消息连接", token);
            try {
                //验证token 信息是否合法
                String userId = JWT.decode(token).getAudience().get(0);
                tokenService.verifier(userId).verify(token);
                String type = tokenService.getType(token);
                if (TOKEN_STUDENT.equals(type)) {
                    //是学生获取学生id
                    attributes.put("uId", tokenService.getStudentId(token));
                    attributes.put("type", TOKEN_STUDENT);
                } else if (TOKEN_TEACHER.equals(type)) {
                    attributes.put("uId", userId);
                    attributes.put("type", TOKEN_TEACHER);
                }
            } catch (JWTVerificationException e) {
                throw new TokenExpiredException("非法 token : [" + token + "] 请求, 错误消息 : [" + e.getMessage() + "]");
            }
            return true;
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        String token = request.getHeaders().getFirst("token");
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
            HttpServletResponse resp = ((ServletServerHttpResponse) response).getServletResponse();
            if (!StringUtils.isEmpty(req.getHeader("sec-websocket-protocol"))) {
                try {
                    System.out.println(URLDecoder.decode(req.getHeader("sec-websocket-protocol"),"utf-8"));
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                resp.addHeader("sec-websocket-protocol", req.getHeader("sec-websocket-protocol"));
            }
        }
        log.info("token : {} 建立连接成功", token);
    }

//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        log.info("用户 : {}  / 用户类型 : {} 进入系统");
//        Map<String, Object> map = session.getAttributes();
//        for (String key : map.keySet()){
//            log.info("key : {},  value : {}", key, map.get(key));
//        }
//    }
}
