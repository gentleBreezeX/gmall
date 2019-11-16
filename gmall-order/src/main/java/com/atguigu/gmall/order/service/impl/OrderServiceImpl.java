package com.atguigu.gmall.order.service.impl;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.bean.UserInfo;
import com.atguigu.gmall.cart.api.GmallCartApi;
import com.atguigu.gmall.cart.vo.CartItemVO;
import com.atguigu.gmall.order.feign.*;
import com.atguigu.gmall.order.interceptor.LoginInterceptor;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.order.vo.OrderConfirmVO;
import com.atguigu.gmall.order.vo.OrderItemVO;
import com.atguigu.gmall.pms.api.GmallPmsApi;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.sms.vo.ItemSaleVO;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author breeze
 * @date 2019/11/15 19:41
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private GmallUmsFeign gmallUmsFeign;
    @Autowired
    private GmallPmsFeign gmallPmsFeign;
    @Autowired
    private GmallWmsFeign gmallWmsFeign;
    @Autowired
    private GmallSmsFeign gmallSmsFeign;
    @Autowired
    private GmallCartFeign gmallCartFeign;
    @Autowired
    private ThreadPoolExecutor threadPool;

    @Override
    public OrderConfirmVO confirm() {

        OrderConfirmVO orderConfirmVO = new OrderConfirmVO();

        //从拦截器获取userId
        UserInfo userInfo = LoginInterceptor.get();
        Long userId = userInfo.getUserId();

        //获取收货地址
        CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {
            Resp<List<MemberReceiveAddressEntity>> addressResp = this.gmallUmsFeign.queryAddressByUserId(userId);
            orderConfirmVO.setAddress(addressResp.getData());
        }, threadPool);

        //获取购物车中选中记录
        CompletableFuture<Void> cartFuture = CompletableFuture.supplyAsync(() -> {
            Resp<List<CartItemVO>> cartItemResp = this.gmallCartFeign.queryCartItemVO(userId);
            return cartItemResp.getData();
        }, threadPool).thenAcceptAsync(cartItemVOS -> {
            //判断是否为空
            if (CollectionUtils.isEmpty(cartItemVOS)) {
                return;
            }

            //把购物车选中记录转化成订货清单
            List<OrderItemVO> orderItems = cartItemVOS.stream().map(cartItemVO -> {
                OrderItemVO orderItemVO = new OrderItemVO();

                orderItemVO.setCount(cartItemVO.getCount());
                orderItemVO.setSkuId(cartItemVO.getSkuId());

                Resp<SkuInfoEntity> skuInfoEntityResp = this.gmallPmsFeign.querySkuinfoBySkuId(orderItemVO.getSkuId());
                SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();

                orderItemVO.setDefaultImage(skuInfoEntity.getSkuDefaultImg());
                orderItemVO.setPrice(skuInfoEntity.getPrice());
                orderItemVO.setSkuTitle(skuInfoEntity.getSkuTitle());
                orderItemVO.setWeight(skuInfoEntity.getWeight());

                Resp<List<SkuSaleAttrValueEntity>> skuSaleAttrResp = this.gmallPmsFeign.querySkuSaleAttrBySkuId(orderItemVO.getSkuId());
                orderItemVO.setSkuAttrValue(skuSaleAttrResp.getData());

                Resp<List<ItemSaleVO>> saleResp = this.gmallSmsFeign.queryItemSaleVOs(cartItemVO.getSkuId());
                orderItemVO.setSales(saleResp.getData());

                Resp<List<WareSkuEntity>> storeResp = this.gmallWmsFeign.queryWareBySkuId(cartItemVO.getSkuId());
                orderItemVO.setStore(storeResp.getData().stream().anyMatch(store -> store.getStock() > 0));

                return orderItemVO;
            }).collect(Collectors.toList());

            orderConfirmVO.setOrderItems(orderItems);
        }, threadPool);


        //设置积分信息
        CompletableFuture<Void> boundFuture = CompletableFuture.runAsync(() -> {
            Resp<MemberEntity> memberEntityResp = this.gmallUmsFeign.queryUserById(userId);
            MemberEntity memberEntity = memberEntityResp.getData();
            orderConfirmVO.setBounds(memberEntity.getIntegration());
        }, threadPool);

        //生成唯一标志，防止重复提交
        CompletableFuture<Void> orderTokenFuture = CompletableFuture.runAsync(() -> {
            String timeId = IdWorker.getTimeId();
            orderConfirmVO.setOrderToken(timeId);
        }, threadPool);

        CompletableFuture.allOf(addressFuture, cartFuture,
                boundFuture, orderTokenFuture).join();

        return orderConfirmVO;
    }
}
