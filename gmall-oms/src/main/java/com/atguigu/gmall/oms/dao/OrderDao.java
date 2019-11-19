package com.atguigu.gmall.oms.dao;

import com.atguigu.gmall.oms.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author breeze
 * @email gentle@breeze.com
 * @date 2019-10-28 18:23:46
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    int closeOrder(String orderToken);

    int success(String orderToken);
}
