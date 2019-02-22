package com.forteach.websocket.config;

import com.forteach.websocket.service.TokenService;
import com.forteach.websocket.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/2  17:02
 */
@Slf4j
@Configuration
public class SimpleCORSFilter implements Filter {

    @Resource
    private TokenService tokenService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest requestToUse = (HttpServletRequest) servletRequest;
        HttpServletResponse responseToUse = (HttpServletResponse) servletResponse;
        responseToUse.addHeader("Sec-WebSocket-Protocol", requestToUse.getHeader("Sec-WebSocket-Protocol"));
        String token = requestToUse.getHeader("token");
        if (StringUtil.isNotEmpty(token)){
            //验证token
            tokenService.validate(token);
            filterChain.doFilter(requestToUse, responseToUse);
        }
    }
}
