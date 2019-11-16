package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.ums.api.GamllUmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author breeze
 * @date 2019/11/15 19:45
 */
@FeignClient("ums-service")
public interface GmallUmsFeign extends GamllUmsApi {
}
