package com.atguigu.gmall.search;

import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.gmall.pms.vo.SpuAttributeValueVO;
import com.atguigu.gmall.search.feign.GmallPmsFeign;
import com.atguigu.gmall.search.feign.GmallWmsFeign;
import com.atguigu.gmall.search.vo.GoodsVO;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;

@SpringBootTest
class GmallSearchApplicationTests {


    @Autowired
    private JestClient jestClient;

    @Autowired
    private GmallPmsFeign gmallPmsFeign;

    @Autowired
    private GmallWmsFeign gmallWmsFeign;

    /**
     * kibana导入检索数据
     * @throws Exception
     */
    @Test
    void importData() throws Exception{

        Long pageNum = 1L;
        Long pageSize = 100L;

        do {
            //分页查询spu
            QueryCondition condition = new QueryCondition();
            condition.setLimit(pageSize);
            condition.setPage(pageNum);

            Resp<List<SpuInfoEntity>> listResp = this.gmallPmsFeign.querySpuPage(condition);
            //获取当前页的spuinfo数据
            List<SpuInfoEntity> spuInfoEntities = listResp.getData();

            //遍历spu获取spu下的所有sku导入到es的索引库中
            for (SpuInfoEntity spuInfoEntity : spuInfoEntities) {
                Resp<List<SkuInfoEntity>> skuInfoResp = this.gmallPmsFeign.listSkuInfo(spuInfoEntity.getId());

                List<SkuInfoEntity> skuInfoEntities = skuInfoResp.getData();

                if (CollectionUtils.isEmpty(skuInfoEntities)){
                    continue;
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
                    Resp<List<SpuAttributeValueVO>> searchAttrValueResp = this.gmallPmsFeign.listSearchAttrValue(spuInfoEntity.getId());
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

            }

            //获取当前页的记录数
            pageSize = (long) spuInfoEntities.size();
            //下一页
            pageNum++;
        } while (pageSize == 100);


    }




    @Test
    void contextLoads() {
    }

}
