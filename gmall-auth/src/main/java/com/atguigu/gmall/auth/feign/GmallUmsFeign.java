package com.atguigu.gmall.auth.feign;

import com.atguigu.gmall.ums.api.GamllUmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author breeze
 * @date 2019/11/12 16:37
 */
@FeignClient("ums-service")
public interface GmallUmsFeign extends GamllUmsApi {

}
