package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author breeze
 * @date 2019/10/31 12:02
 *
 * spuInfo扩展对象
 * 包含：spuInfo基本信息、spuImages图片信息、baseAttrs基本属性信息、skus信息
 *
 * 9张表：
 *  pms_product_attr_value
 *  pms_spu_info
 *  pms_spu_images
 *
 *  pms_sku_info
 *  pms_sku_images
 *  pms_sku_sale_attr_value
 *
 *  sms_sku_bounds
 *  sms_sku_full_reduction
 *  sms_sku_ladder
 *
 */
@Data
public class SpuInfoVO extends SpuInfoEntity {

    @ApiModelProperty(name = "spuImages", value = "spu图片集")
    private List<String> spuImages;

    private List<ProductAttrValueVO> baseAttrs;

    private List<SkuInfoVO> skus;
}
