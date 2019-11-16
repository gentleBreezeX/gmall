package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.cart.api.GmallCartApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author breeze
 * @date 2019/11/15 20:14
 */
@FeignClient("cart-service")
public interface GmallCartFeign extends GmallCartApi {
}
