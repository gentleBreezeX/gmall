package com.atguigu.gmall.item.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author breeze
 * @date 2019/11/10 14:37
 */
@FeignClient("pms-service")
public interface GmallPmsFeign extends GmallPmsApi {
}
