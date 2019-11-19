package com.atguigu.gmall.oms.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.vo.OrderSubmitVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author breeze
 * @date 2019/11/17 17:24
 */
public interface GmallOmsApi {

    @PostMapping("oms/order")
    Resp<OrderEntity> creatOrder(@RequestBody OrderSubmitVO orderSubmitVO);
}
