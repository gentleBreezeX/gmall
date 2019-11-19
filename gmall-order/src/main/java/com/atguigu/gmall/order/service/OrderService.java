package com.atguigu.gmall.order.service;

import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.vo.OrderSubmitVO;
import com.atguigu.gmall.order.vo.OrderConfirmVO;

/**
 * @author breeze
 * @date 2019/11/15 19:40
 */
public interface OrderService {
    OrderConfirmVO confirm();

    OrderEntity submit(OrderSubmitVO submitVO);

    void paySuccess(String out_trade_no);
}
