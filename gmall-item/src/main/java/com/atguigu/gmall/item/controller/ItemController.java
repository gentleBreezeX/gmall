package com.atguigu.gmall.item.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.item.vo.ItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author breeze
 * @date 2019/11/10 14:32
 */
@RestController
@RequestMapping("item")
public class ItemController {

    @Autowired
    private ItemService itemService;


    @GetMapping("{skuId}")
    public Resp<ItemVO> listItem(@PathVariable("skuId")Long skuId){

        ItemVO item = itemService.listItem(skuId);

        return Resp.ok(item);
    }



}


