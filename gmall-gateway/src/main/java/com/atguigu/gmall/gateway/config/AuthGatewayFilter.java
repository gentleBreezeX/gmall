package com.atguigu.gmall.gateway.config;

import com.atguigu.core.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * @author breeze
 * @date 2019/11/12 19:09
 */
@Component
@EnableConfigurationProperties(JwtProperties.class)
public class AuthGatewayFilter implements GatewayFilter, Ordered {

    @Autowired
    private JwtProperties jwtProperties;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //1.获取过滤器中提供的req和res
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        //2.获取cookie
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        //3.判断是否存在，不存在重定向回登录页面
        if (cookies == null || !cookies.containsKey(jwtProperties.getCookieName())) {
            //设置相应状态码未认证
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //4.存在，解析
        HttpCookie cookie = cookies.getFirst(jwtProperties.getCookieName());

        try {
            JwtUtils.getInfoFromToken(cookie.getValue(), jwtProperties.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
            //设置相应状态码未认证
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        return chain.filter(exchange);
    }


    @Override
    public int getOrder() {
        return 0;
    }
}
