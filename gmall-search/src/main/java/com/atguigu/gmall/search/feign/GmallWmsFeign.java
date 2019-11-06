package com.atguigu.gmall.search.feign;

import com.atguigu.gmall.wms.api.GmallWmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author breeze
 * @date 2019/11/5 23:12
 */
@FeignClient("wms-service")
public interface GmallWmsFeign extends GmallWmsApi {
}
