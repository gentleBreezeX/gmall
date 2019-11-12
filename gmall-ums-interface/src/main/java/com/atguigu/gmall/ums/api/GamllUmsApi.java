package com.atguigu.gmall.ums.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.ums.entity.MemberEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author breeze
 * @date 2019/11/12 16:28
 */
public interface GamllUmsApi {

    @GetMapping("ums/member/query")
    Resp<MemberEntity> queryUserByUsernameAndPassword(
            @RequestParam("username")String username, @RequestParam("password")String password);

}
