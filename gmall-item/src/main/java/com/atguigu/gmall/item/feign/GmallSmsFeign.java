package com.atguigu.gmall.item.feign;


import com.atguigu.gmall.sms.feign.SkuSaleFeignApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author breeze
 * @date 2019/11/10 14:38
 */
@FeignClient("sms-service")
public interface GmallSmsFeign extends SkuSaleFeignApi {
}
