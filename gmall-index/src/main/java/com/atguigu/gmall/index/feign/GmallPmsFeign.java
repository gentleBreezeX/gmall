package com.atguigu.gmall.index.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author breeze
 * @date 2019/11/8 16:57
 */
@FeignClient("pms-service")
public interface GmallPmsFeign extends GmallPmsApi {
}
