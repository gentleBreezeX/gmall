package com.atguigu.gmall.pms.dao;

import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * spu属性值
 * 
 * @author breeze
 * @email gentle@breeze.com
 * @date 2019-10-28 18:25:21
 */
@Mapper
public interface ProductAttrValueDao extends BaseMapper<ProductAttrValueEntity> {

    List<ProductAttrValueEntity> listSearchAttrValue(Long spuId);

    List<ProductAttrValueEntity> queryByGidAndSpuId(@Param("spuId") Long spuId, @Param("groupId")Long groupId);

}
