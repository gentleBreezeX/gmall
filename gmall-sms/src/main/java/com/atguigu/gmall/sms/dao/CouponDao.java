package com.atguigu.gmall.sms.dao;

import com.atguigu.gmall.sms.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author breeze
 * @email gentle@breeze.com
 * @date 2019-10-28 18:26:31
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
