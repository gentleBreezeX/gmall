package com.atguigu.gmall.sms.service;

import com.atguigu.gmall.sms.dto.SkuSaleDTO;
import com.atguigu.gmall.sms.vo.ItemSaleVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.List;


/**
 * 商品sku积分设置
 *
 * @author breeze
 * @email gentle@breeze.com
 * @date 2019-10-28 18:26:31
 */
public interface SkuBoundsService extends IService<SkuBoundsEntity> {

    PageVo queryPage(QueryCondition params);

    void saveSkuSaleInfo(SkuSaleDTO skuSaleDTO);

    List<ItemSaleVO> queryItemSaleVOs(Long skuId);
}

