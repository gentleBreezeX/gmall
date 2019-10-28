package com.atguigu.gmall.ums.dao;

import com.atguigu.gmall.ums.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author breeze
 * @email gentle@breeze.com
 * @date 2019-10-28 18:27:30
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
