package com.atguigu.gmall.pms.api;

import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.CategoryVO;
import com.atguigu.gmall.pms.vo.GroupVO;
import com.atguigu.gmall.pms.vo.SpuAttributeValueVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author breeze
 * @date 2019/11/5 22:31
 */
public interface GmallPmsApi {

    @GetMapping("pms/skusaleattrvalue/sku/{skuId}")
    Resp<List<SkuSaleAttrValueEntity>> querySkuSaleAttrBySkuId(@PathVariable("skuId") Long skuId);

    @GetMapping("pms/skuinfo/info/{skuId}")
    Resp<SkuInfoEntity> querySkuinfoBySkuId(@PathVariable("skuId") Long skuId);

    @GetMapping("pms/brand/info/{brandId}")
    Resp<BrandEntity> listBrandById(@PathVariable("brandId") Long brandId);

    @GetMapping("pms/category/info/{catId}")
    Resp<CategoryEntity> listCaregoryById(@PathVariable("catId") Long catId);

    @GetMapping("pms/spuinfo/info/{id}")
    Resp<SpuInfoEntity> querySpuinfoById(@PathVariable("id") Long id);

    @GetMapping("pms/skuimages/{skuId}")
    Resp<List<String>> querySkuImagesBySkuId(@PathVariable("skuId")Long skuId);

    @GetMapping("pms/skusaleattrvalue/{spuId}")
    Resp<List<SkuSaleAttrValueEntity>> querySaleAttrValues(@PathVariable("spuId") Long spuId);

    @GetMapping("pms/spuinfodesc/info/{spuId}")
    Resp<SpuInfoDescEntity> querySpuDescBySpuId(@PathVariable("spuId") Long spuId);

    @GetMapping("pms/attrgroup/item/group/{cid}/{spuId}")
    Resp<List<GroupVO>> queryGroupVOByCid(@PathVariable("cid")Long cid, @PathVariable("spuId")Long spuId);



    @PostMapping("pms/spuinfo/list")
    Resp<List<SpuInfoEntity>> querySpuPage(@RequestBody QueryCondition queryCondition);

    @GetMapping("pms/skuinfo/{spuId}")
    Resp<List<SkuInfoEntity>> listSkuInfo(@PathVariable("spuId")Long spuId);

    @GetMapping("pms/category")
    Resp<List<CategoryEntity>> listCategory(
            @RequestParam(value = "level", defaultValue = "0")Integer level,
            @RequestParam(value = "parentCid", required = false)Long parentCid);

    @GetMapping("pms/category/{pid}")
    Resp<List<CategoryVO>> listChildrenCate(@PathVariable("pid")Long pid);

    @GetMapping("pms/productattrvalue/{spuId}")
    Resp<List<SpuAttributeValueVO>> listSearchAttrValue(@PathVariable("spuId") Long spuId);
}