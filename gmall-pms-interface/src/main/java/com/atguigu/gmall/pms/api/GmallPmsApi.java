package com.atguigu.gmall.pms.api;

import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.gmall.pms.vo.CategoryVO;
import com.atguigu.gmall.pms.vo.SpuAttributeValueVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author breeze
 * @date 2019/11/5 22:31
 */
public interface GmallPmsApi {


    @PostMapping("pms/spuinfo/list")
    Resp<List<SpuInfoEntity>> querySpuPage(@RequestBody QueryCondition queryCondition);

    @GetMapping("pms/skuinfo/{spuId}")
    Resp<List<SkuInfoEntity>> listSkuInfo(@PathVariable("spuId")Long spuId);

    @GetMapping("pms/brand/info/{brandId}")
    Resp<BrandEntity> listBrandById(@PathVariable("brandId") Long brandId);

    @GetMapping("pms/category/info/{catId}")
    Resp<CategoryEntity> listCaregoryById(@PathVariable("catId") Long catId);

    @GetMapping("pms/category")
    Resp<List<CategoryEntity>> listCategory(
            @RequestParam(value = "level", defaultValue = "0")Integer level,
            @RequestParam(value = "parentCid", required = false)Long parentCid);

    @GetMapping("pms/category/{pid}")
    Resp<List<CategoryVO>> listChildrenCate(@PathVariable("pid")Long pid);

    @GetMapping("pms/productattrvalue/{spuId}")
    Resp<List<SpuAttributeValueVO>> listSearchAttrValue(@PathVariable("spuId") Long spuId);
}