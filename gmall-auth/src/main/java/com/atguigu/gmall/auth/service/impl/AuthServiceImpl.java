package com.atguigu.gmall.auth.service.impl;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.utils.JwtUtils;
import com.atguigu.gmall.auth.config.JwtProperties;
import com.atguigu.gmall.auth.exception.GmallException;
import com.atguigu.gmall.auth.feign.GmallUmsFeign;
import com.atguigu.gmall.auth.service.AuthService;
import com.atguigu.gmall.ums.entity.MemberEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @author breeze
 * @date 2019/11/12 17:57
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private GmallUmsFeign gmallUmsFeign;
    @Autowired
    private JwtProperties jwtProperties;


    @Override
    public String authentication(String username, String password) {

        try {
            //1.调用微服务，查询用户名密码是否正确
            Resp<MemberEntity> memberEntityResp = this.gmallUmsFeign.queryUserByUsernameAndPassword(username, password);
            MemberEntity memberEntity = memberEntityResp.getData();
            //2.查询结果为null直接返回
            if (memberEntity == null) {
                return null;
            }
            //3.如果有查询结果，生成token
            HashMap<String, Object> map = new HashMap<>(2);
            map.put("id", memberEntity.getId());
            map.put("username", memberEntity.getUsername());
            String token = JwtUtils.generateToken(map, jwtProperties.getPrivateKey(), jwtProperties.getExpire());

            return token;
        } catch (Exception e) {
            throw new GmallException("生成token失败" + e);
        }
    }
}
