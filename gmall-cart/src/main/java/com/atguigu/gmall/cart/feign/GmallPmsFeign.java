package com.atguigu.gmall.cart.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author breeze
 * @date 2019/11/13 16:23
 */
@FeignClient("pms-service")
public interface GmallPmsFeign extends GmallPmsApi {
}
