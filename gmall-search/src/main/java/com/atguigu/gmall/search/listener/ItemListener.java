package com.atguigu.gmall.search.listener;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.vo.SpuAttributeValueVO;
import com.atguigu.gmall.search.feign.GmallPmsFeign;
import com.atguigu.gmall.search.feign.GmallWmsFeign;
import com.atguigu.gmall.search.vo.GoodsVO;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import io.searchbox.client.JestClient;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author breeze
 * @date 2019/11/7 18:03
 */
@Component
public class ItemListener {

    @Autowired
    private JestClient jestClient;

    @Autowired
    private GmallPmsFeign gmallPmsFeign;

    @Autowired
    private GmallWmsFeign gmallWmsFeign;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "GMALL-SEARCH-QUEUE", durable = "true"),
            exchange = @Exchange(value = "gmall.item.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = {"item.*"}
    ))
    public void listener(Map<String, Object> map){

        if (CollectionUtils.isEmpty(map)) {
            return;
        }

        Long spuId = (Long) map.get("spuId");
        String type = map.get("type").toString();

        if (StringUtils.equals("insert", type) || StringUtils.equals("update", type)){

            Resp<List<SkuInfoEntity>> skuInfoResp = this.gmallPmsFeign.listSkuInfo(spuId);

            List<SkuInfoEntity> skuInfoEntities = skuInfoResp.getData();

            if (CollectionUtils.isEmpty(skuInfoEntities)){
                return;
            }

            skuInfoEntities.forEach(skuInfoEntity -> {
                GoodsVO goodsVo = new GoodsVO();
                //设置sku相关的数据
                goodsVo.setName(skuInfoEntity.getSkuTitle());
                goodsVo.setId(skuInfoEntity.getSkuId());
                goodsVo.setPic(skuInfoEntity.getSkuDefaultImg());
                goodsVo.setPrice(skuInfoEntity.getPrice());
                goodsVo.setSale(100);
                goodsVo.setSort(0);//综合排序

                //设置品牌相关的
                Resp<BrandEntity> brandEntityResp = this.gmallPmsFeign.listBrandById(skuInfoEntity.getBrandId());
                BrandEntity brandEntity = brandEntityResp.getData();
                if (brandEntity != null) {
                    goodsVo.setBrandId(skuInfoEntity.getBrandId());
                    goodsVo.setBrandName(brandEntity.getName());
                }

                //设置分类相关相关的
                Resp<CategoryEntity> categoryEntityResp = this.gmallPmsFeign.listCaregoryById(skuInfoEntity.getCatalogId());
                CategoryEntity categoryEntity = categoryEntityResp.getData();
                if (categoryEntity != null) {
                    goodsVo.setProductCategoryId(skuInfoEntity.getCatalogId());
                    goodsVo.setProductCategoryName(categoryEntity.getName());
                }

                //设置搜索属性
                Resp<List<SpuAttributeValueVO>> searchAttrValueResp = this.gmallPmsFeign.listSearchAttrValue(spuId);
                List<SpuAttributeValueVO> spuAttributeValueVOList = searchAttrValueResp.getData();
                goodsVo.setAttrValueList(spuAttributeValueVOList);

                //库存
                Resp<List<WareSkuEntity>> wareSkuResp = this.gmallWmsFeign.get(skuInfoEntity.getSkuId());
                List<WareSkuEntity> wareSkuEntities = wareSkuResp.getData();

                //只要有一个库存大于0就返回true，可能有多个仓库
                if (wareSkuEntities.stream().anyMatch(t -> t.getStock() > 0)) {
                    goodsVo.setStock(1L);
                } else {
                    goodsVo.setStock(0L);
                }

                Index index = new Index.Builder(goodsVo).index("goods").type("info")
                        .id(skuInfoEntity.getSkuId().toString()).build();

                try {
                    this.jestClient.execute(index);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });


        } else if (StringUtils.equals("delete", type)) {

            Resp<List<SkuInfoEntity>> skuInfoResp = this.gmallPmsFeign.listSkuInfo(spuId);

            List<SkuInfoEntity> skuInfoEntities = skuInfoResp.getData();

            if (CollectionUtils.isEmpty(skuInfoEntities)){
                return;
            }

            skuInfoEntities.forEach(skuInfoEntity -> {

                Delete delete = new Delete.Builder(skuInfoEntity.getSkuId().toString())
                        .index("goods").type("info").build();

                try {
                    this.jestClient.execute(delete);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        }
    }
}
