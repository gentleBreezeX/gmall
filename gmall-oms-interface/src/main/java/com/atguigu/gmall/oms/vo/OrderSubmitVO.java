package com.atguigu.gmall.oms.vo;

import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author breeze
 * @date 2019/11/15 21:04
 */
@Data
public class OrderSubmitVO {

    private Long userId;
    private String userName;

    private MemberReceiveAddressEntity address; //收货地址

    private Integer payType; //支付方式

    private String deliveryCompany; //配送方式

    private List<OrderItemVO> orderItemVOS; //订单商品信息

    private Integer userIntegration; //下单时使用积分信息

    private BigDecimal totalPrice; //总价

    private String orderToken; //防重提交，订单编号
}
