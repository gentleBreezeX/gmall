package com.atguigu.gmall.item.feign;

import com.atguigu.gmall.wms.api.GmallWmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author breeze
 * @date 2019/11/10 14:40
 */
@FeignClient("wms-service")
public interface GmallWmsFeign extends GmallWmsApi {
}
