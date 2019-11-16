package com.atguigu.gmall.wms.vo;

import lombok.Data;

/**
 * @author breeze
 * @date 2019/11/15 22:49
 */
@Data
public class SkuLockVO {

    private Long skuId;

    private Integer count;

    private Boolean lock; //商品锁定成功true

    private Long skuWareId; //锁定库存的id
}
