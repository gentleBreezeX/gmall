package com.atguigu.gmall.sms.feign;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.sms.dto.SkuSaleDTO;
import com.atguigu.gmall.sms.vo.ItemSaleVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author breeze
 * @date 2019/11/1 11:43
 */
public interface SkuSaleFeignApi {


    @GetMapping("sms/skubounds/item/sales/{skuId}")
    public Resp<List<ItemSaleVO>> queryItemSaleVOs(@PathVariable("skuId")Long skuId);

    @PostMapping("/sms/skubounds/skusale/save")
    public Resp<Object> saveSkuSaleInfo(@RequestBody SkuSaleDTO skuSaleDTO);
}
