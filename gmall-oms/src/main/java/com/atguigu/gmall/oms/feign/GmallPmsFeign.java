package com.atguigu.gmall.oms.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author breeze
 * @date 2019/11/17 17:18
 */
@FeignClient("pms-service")
public interface GmallPmsFeign extends GmallPmsApi {
}
