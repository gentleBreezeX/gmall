package com.atguigu.gmall.auth.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.utils.CookieUtils;
import com.atguigu.gmall.auth.config.JwtProperties;
import com.atguigu.gmall.auth.service.AuthService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author breeze
 * @date 2019/11/12 16:17
 */
@RestController
@RequestMapping("auth")
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("accredit")
    public Resp<Object> authentication(@RequestParam("username")String username,
                                       @RequestParam("password")String password,
                                       HttpServletRequest request,
                                       HttpServletResponse response){

        String token = this.authService.authentication(username, password);
        if (StringUtils.isBlank(token)) {
            return Resp.fail("登录失败，用户名或密码错误");
        }
        //将token写入到cookie中
        CookieUtils.setCookie(request, response, jwtProperties.getCookieName(),
                token, jwtProperties.getExpire(), "utf-8", true);


        return Resp.ok("登录成功");
    }

}
