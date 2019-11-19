package com.atguigu.gmall.oms.service;

import com.atguigu.gmall.oms.vo.OrderSubmitVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 订单
 *
 * @author breeze
 * @email gentle@breeze.com
 * @date 2019-10-28 18:23:46
 */
public interface OrderService extends IService<OrderEntity> {

    PageVo queryPage(QueryCondition params);

    OrderEntity creatOrder(OrderSubmitVO orderSubmitVO);

    int closeOrder(String orderToken);

    int success(String orderToken);
}

