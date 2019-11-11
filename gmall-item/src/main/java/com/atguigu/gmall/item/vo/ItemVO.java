package com.atguigu.gmall.item.vo;

import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.GroupVO;
import com.atguigu.gmall.sms.vo.ItemSaleVO;
import lombok.Data;

import java.util.List;

/**
 * @author breeze
 * @date 2019/11/10 13:53
 */
@Data
public class ItemVO extends SkuInfoEntity {

    private BrandEntity brand;

    private CategoryEntity category;

    private SpuInfoEntity spuInfo;
    //sku图片列表
    private List<String> pics;
    //营销信息
    private List<ItemSaleVO> sales;
    //是否有货
    private boolean store;
    //spu下所有的销售属性
    private List<SkuSaleAttrValueEntity> skuSales;
    //描述信息
    private SpuInfoDescEntity desc;
    //组及组下的规则属性以及值
    private List<GroupVO> groups;

}
