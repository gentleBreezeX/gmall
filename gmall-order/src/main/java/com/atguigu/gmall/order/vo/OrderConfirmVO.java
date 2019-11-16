package com.atguigu.gmall.order.vo;

import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import lombok.Data;

import java.util.List;

/**
 * @author breeze
 * @date 2019/11/15 19:16
 */
@Data
public class OrderConfirmVO {

    private List<MemberReceiveAddressEntity> address; //收货地址

    private List<OrderItemVO> orderItems; // 订单页面商品信息

    private Integer bounds; // 积分

    private String orderToken; // 唯一标志，防重复提交

}
