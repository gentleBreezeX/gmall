package com.atguigu.gmall.wms.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author breeze
 * @date 2019/11/5 22:35
 */
public interface GmallWmsApi {

    @GetMapping("wms/waresku/{skuId}")
    Resp<List<WareSkuEntity>> queryWareBySkuId(@PathVariable("skuId") Long skuId);

}