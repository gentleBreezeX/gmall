package com.atguigu.gmall.ums.vo;

import lombok.Data;

/**
 * @author breeze
 * @date 2019/11/18 21:00
 */
@Data
public class UserBoundVO {

    private Long userId;

    private Integer growth;//成长积分

    private Integer integration;//购物积分

}
