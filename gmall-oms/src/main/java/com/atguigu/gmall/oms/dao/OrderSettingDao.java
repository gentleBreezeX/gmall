package com.atguigu.gmall.oms.dao;

import com.atguigu.gmall.oms.entity.OrderSettingEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单配置信息
 * 
 * @author breeze
 * @email gentle@breeze.com
 * @date 2019-10-28 18:23:45
 */
@Mapper
public interface OrderSettingDao extends BaseMapper<OrderSettingEntity> {
	
}
