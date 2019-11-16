package com.atguigu.gmall.order.interceptor;

import com.atguigu.core.bean.UserInfo;
import com.atguigu.core.utils.CookieUtils;
import com.atguigu.core.utils.JwtUtils;
import com.atguigu.gmall.order.config.JwtProperties;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author breeze
 * @date 2019/11/13 9:48
 */
@Component
@EnableConfigurationProperties(JwtProperties.class)
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private JwtProperties jwtProperties;

    private static final ThreadLocal<UserInfo> THREAD_LOCAL = new ThreadLocal<>();


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //从cookie获取token和userKey
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());

        UserInfo userInfo = new UserInfo();

        //判断token是否存在
        if (StringUtils.isEmpty(token)) {
            return false;
        }

        //解析token
        try {
            Map<String, Object> map = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());

            userInfo.setUserId(new Long(map.get("id").toString()));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        THREAD_LOCAL.set(userInfo);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //因为我们使用的是线程池，所以用完之后需要移除值
        THREAD_LOCAL.remove();
    }

    public static UserInfo get(){
        return THREAD_LOCAL.get();
    }

}
