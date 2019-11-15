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

    private Long skuId; //商品id

    private String skuTitle; //商品标题

    private Boolean check; //是否勾选

    private Integer count; //数量

    private String defaultImage; // 默认图片

    private BigDecimal price; //价格

    private BigDecimal currentPrice; // 当前价格(最新价格)

    private List<ItemSaleVO> sales; //营销信息优惠积分

    private List<SkuSaleAttrValueEntity> skuAttrValue; //销售属性，规格参数
}
