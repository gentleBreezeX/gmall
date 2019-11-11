package com.atguigu.gmall.item.service.impl;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.item.feign.GmallPmsFeign;
import com.atguigu.gmall.item.feign.GmallSmsFeign;
import com.atguigu.gmall.item.feign.GmallWmsFeign;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.item.vo.ItemVO;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.GroupVO;
import com.atguigu.gmall.sms.vo.ItemSaleVO;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author breeze
 * @date 2019/11/10 14:34
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private GmallPmsFeign gmallPmsFeign;
    @Autowired
    private GmallWmsFeign gmallWmsFeign;
    @Autowired
    private GmallSmsFeign gmallSmsFeign;


    @Override
    public ItemVO listItem(Long skuId) {

        ItemVO itemVO = new ItemVO();

        //1.查询sku信息
        Resp<SkuInfoEntity> skuInfoEntityResp = this.gmallPmsFeign.querySkuinfoBySkuId(skuId);
        SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
        BeanUtils.copyProperties(skuInfoEntity, itemVO);

        //2.品牌
        Resp<BrandEntity> brandEntityResp = this.gmallPmsFeign.listBrandById(skuInfoEntity.getBrandId());
        itemVO.setBrand(brandEntityResp.getData());

        //3.分类
        Resp<CategoryEntity> categoryEntityResp = this.gmallPmsFeign.listCaregoryById(skuInfoEntity.getCatalogId());
        itemVO.setCategory(categoryEntityResp.getData());

        //4.spu信息
        Resp<SpuInfoEntity> spuInfoEntityResp = this.gmallPmsFeign.querySpuinfoById(skuInfoEntity.getSpuId());
        itemVO.setSpuInfo(spuInfoEntityResp.getData());

        //5.设置图片信息
        Resp<List<String>> skuImagesResp = this.gmallPmsFeign.querySkuImagesBySkuId(skuId);
        itemVO.setPics(skuImagesResp.getData());

        //6.营销信息
        Resp<List<ItemSaleVO>> itemSaleResp = this.gmallSmsFeign.queryItemSaleVOs(skuId);
        itemVO.setSales(itemSaleResp.getData());

        //7.是否有货
        Resp<List<WareSkuEntity>> wareSkuResp = this.gmallWmsFeign.get(skuId);
        List<WareSkuEntity> wareSkuEntities = wareSkuResp.getData();
        itemVO.setStore(wareSkuEntities.stream().anyMatch(t -> t.getStock() > 0));

        //8.spu所有的销售属性
        Resp<List<SkuSaleAttrValueEntity>> saleAttrValuesResp = this.gmallPmsFeign.querySaleAttrValues(skuInfoEntity.getSpuId());
        itemVO.setSkuSales(saleAttrValuesResp.getData());

        //9.spu的描述信息
        Resp<SpuInfoDescEntity> spuInfoDescEntityResp = this.gmallPmsFeign.querySpuDescBySpuId(skuInfoEntity.getSpuId());
        itemVO.setDesc(spuInfoDescEntityResp.getData());

        //10.规格属性
        Resp<List<GroupVO>> groupVOResp = this.gmallPmsFeign.queryGroupVOByCid(skuInfoEntity.getCatalogId(), skuInfoEntity.getSpuId());
        itemVO.setGroups(groupVOResp.getData());


        return itemVO;
    }
}
