package com.atguigu.gmall.cart.vo;

import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.sms.vo.ItemSaleVO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author breeze
 * @date 2019/11/13 16:17
 * 购物车实体类
 */
@Data
public class Cart {

    private Long skuId;

    private String skuTitle;

    private Boolean check;

    private Integer count;

    private String defaultImage;

    private BigDecimal price;

    private List<ItemSaleVO> sales;

    private List<SkuSaleAttrValueEntity> skuAttrValue;
}
