package com.atguigu.gmall.order.vo;

import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.sms.vo.ItemSaleVO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author breeze
 * @date 2019/11/15 19:22
 */
@Data
public class OrderItemVO {

    private Long skuId; //商品id

    private String skuTitle; //商品标题

    private Integer count; //数量

    private String defaultImage; // 默认图片

    private BigDecimal price; //价格

    private Boolean store; //是否有货

    private BigDecimal weight; //重量

    private List<ItemSaleVO> sales; //营销信息优惠积分

    private List<SkuSaleAttrValueEntity> skuAttrValue; //销售属性，规格参数

}
