package com.atguigu.gmall.cart.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.cart.vo.Cart;
import com.atguigu.gmall.cart.vo.CartItemVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author breeze
 * @date 2019/11/13 16:10
 */
@RestController
@RequestMapping("cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @ApiOperation("获取用户选中的skuId和数量")
    @GetMapping("order/{userId}")
    public Resp<List<CartItemVO>> queryCartItemVO(@PathVariable("userId")Long userId){

        List<CartItemVO> cartItemVOList = this.cartService.queryCartItemVO(userId);

        return Resp.ok(cartItemVOList);
    }

    @ApiOperation("添加商品到购物车的方法")
    @PostMapping
    public Resp<Object> addCart(@RequestBody Cart cart){

        this.cartService.addCart(cart);

        return Resp.ok("添加购物车成功");
    }

    @ApiOperation("查看购物车的方法")
    @GetMapping
    public Resp<List<Cart>> queryCarts(){
        List<Cart> carts = this.cartService.queryCarts();
        return Resp.ok(carts);
    }

    @ApiOperation("修改购物车商品数量")
    @PostMapping("update")
    public Resp<Object> updateCart(@RequestBody Cart cart){

        this.cartService.updateCart(cart);

        return Resp.ok(null);
    }

    @ApiOperation("删除购物车商品")
    @PostMapping("{skuId}")
    public Resp<Object> removeCart(@PathVariable("skuId")Long skuId){

        this.cartService.removeCart(skuId);

        return Resp.ok(null);
    }

    @ApiOperation("商品选中的方法")
    @PostMapping("check")
    public Resp<Object> checkCart(@RequestBody List<Cart> carts) {

        this.cartService.checkCart(carts);

        return Resp.ok(null);
    }

}
