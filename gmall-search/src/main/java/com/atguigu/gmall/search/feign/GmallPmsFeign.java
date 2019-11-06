package com.atguigu.gmall.search.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author breeze
 * @date 2019/11/5 23:12
 */
@FeignClient("pms-service")
public interface GmallPmsFeign extends GmallPmsApi {
}
