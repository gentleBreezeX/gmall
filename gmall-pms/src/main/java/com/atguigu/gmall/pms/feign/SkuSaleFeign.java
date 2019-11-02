package com.atguigu.gmall.pms.feign;

import com.atguigu.gmall.sms.feign.SkuSaleFeignApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author breeze
 * @date 2019/10/31 16:31
 */
@FeignClient("sms-service")
public interface SkuSaleFeign extends SkuSaleFeignApi {


}
