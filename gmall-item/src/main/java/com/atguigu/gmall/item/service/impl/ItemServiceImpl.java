package com.atguigu.gmall.item.service.impl;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.item.config.ThreadPoolConfig;
import com.atguigu.gmall.item.feign.GmallPmsFeign;
import com.atguigu.gmall.item.feign.GmallSmsFeign;
import com.atguigu.gmall.item.feign.GmallWmsFeign;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.item.vo.ItemVO;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.GroupVO;
import com.atguigu.gmall.sms.vo.ItemSaleVO;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.sun.xml.internal.ws.util.CompletedFuture;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

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
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;


    @Override
    public ItemVO listItem(Long skuId) {

        ItemVO itemVO = new ItemVO();

        //创建异步编排对象
        CompletableFuture<SkuInfoEntity> skuCompletableFuture = CompletableFuture.supplyAsync(() -> {
            //1.查询sku信息
            Resp<SkuInfoEntity> skuInfoEntityResp = this.gmallPmsFeign.querySkuinfoBySkuId(skuId);
            SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
            BeanUtils.copyProperties(skuInfoEntity, itemVO);
            return skuInfoEntity;
        }, threadPoolExecutor);

        //2.品牌
        CompletableFuture<Void> brandCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            Resp<BrandEntity> brandEntityResp = this.gmallPmsFeign.listBrandById(skuInfoEntity.getBrandId());
            itemVO.setBrand(brandEntityResp.getData());
        }, threadPoolExecutor);

        //3.分类
        CompletableFuture<Void> categoryCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            Resp<CategoryEntity> categoryEntityResp = this.gmallPmsFeign.listCaregoryById(skuInfoEntity.getCatalogId());
            itemVO.setCategory(categoryEntityResp.getData());
        }, threadPoolExecutor);

        //4.spu信息
        CompletableFuture<Void> spuCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            Resp<SpuInfoEntity> spuInfoEntityResp = this.gmallPmsFeign.querySpuinfoById(skuInfoEntity.getSpuId());
            itemVO.setSpuInfo(spuInfoEntityResp.getData());
        }, threadPoolExecutor);

        //5.设置图片信息
        CompletableFuture<Void> picCompletableFuture = CompletableFuture.runAsync(() -> {
            Resp<List<String>> skuImagesResp = this.gmallPmsFeign.querySkuImagesBySkuId(skuId);
            itemVO.setPics(skuImagesResp.getData());
        }, threadPoolExecutor);


        //6.营销信息
        CompletableFuture<Void> saleCompletableFuture = CompletableFuture.runAsync(() -> {
            Resp<List<ItemSaleVO>> itemSaleResp = this.gmallSmsFeign.queryItemSaleVOs(skuId);
            itemVO.setSales(itemSaleResp.getData());
        }, threadPoolExecutor);

        //7.是否有货
        CompletableFuture<Void> storeCompletableFuture = CompletableFuture.runAsync(() -> {
            Resp<List<WareSkuEntity>> wareSkuResp = this.gmallWmsFeign.queryWareBySkuId(skuId);
            List<WareSkuEntity> wareSkuEntities = wareSkuResp.getData();
            itemVO.setStore(wareSkuEntities.stream().anyMatch(t -> t.getStock() > 0));
        }, threadPoolExecutor);


        //8.spu所有的销售属性
        CompletableFuture<Void> spuSaleCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            Resp<List<SkuSaleAttrValueEntity>> saleAttrValuesResp = this.gmallPmsFeign.querySaleAttrValues(skuInfoEntity.getSpuId());
            itemVO.setSkuSales(saleAttrValuesResp.getData());
        }, threadPoolExecutor);

        //9.spu的描述信息
        CompletableFuture<Void> descCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            Resp<SpuInfoDescEntity> spuInfoDescEntityResp = this.gmallPmsFeign.querySpuDescBySpuId(skuInfoEntity.getSpuId());
            itemVO.setDesc(spuInfoDescEntityResp.getData());
        }, threadPoolExecutor);

        //10.规格属性
        CompletableFuture<Void> groupCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            Resp<List<GroupVO>> groupVOResp = this.gmallPmsFeign.queryGroupVOByCid(skuInfoEntity.getCatalogId(), skuInfoEntity.getSpuId());
            itemVO.setGroups(groupVOResp.getData());
        }, threadPoolExecutor);

        CompletableFuture.allOf(skuCompletableFuture, brandCompletableFuture, categoryCompletableFuture,
                spuCompletableFuture, picCompletableFuture, saleCompletableFuture, storeCompletableFuture,
                spuSaleCompletableFuture, descCompletableFuture, groupCompletableFuture).join();

        return itemVO;
    }
}
