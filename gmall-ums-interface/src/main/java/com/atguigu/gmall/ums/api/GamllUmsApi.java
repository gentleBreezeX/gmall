package com.atguigu.gmall.ums.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author breeze
 * @date 2019/11/12 16:28
 */
public interface GamllUmsApi {

    @GetMapping("ums/member/query")
    Resp<MemberEntity> queryUserByUsernameAndPassword(
            @RequestParam("username") String username, @RequestParam("password") String password);

    @GetMapping("ums/memberreceiveaddress/{userId}")
    Resp<List<MemberReceiveAddressEntity>> queryAddressByUserId(@PathVariable("userId") Long userId);

    @GetMapping("ums/member/info/{id}")
    Resp<MemberEntity> queryUserById(@PathVariable("id") Long id);

}
