package com.atguigu.gmall.cart.feign;

import com.atguigu.gmall.sms.feign.SkuSaleFeignApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author breeze
 * @date 2019/11/13 16:24
 */
@FeignClient("sms-service")
public interface GmallSmsFeign extends SkuSaleFeignApi {
}
