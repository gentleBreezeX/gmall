package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author breeze
 * @date 2019/11/15 19:45
 */
@FeignClient("pms-service")
public interface GmallPmsFeign extends GmallPmsApi {
}
