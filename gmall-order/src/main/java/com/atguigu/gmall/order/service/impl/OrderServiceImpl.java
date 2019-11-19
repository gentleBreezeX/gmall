package com.atguigu.gmall.order.service.impl;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.bean.UserInfo;
import com.atguigu.gmall.cart.vo.CartItemVO;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.vo.OrderItemVO;
import com.atguigu.gmall.oms.vo.OrderSubmitVO;
import com.atguigu.gmall.order.feign.*;
import com.atguigu.gmall.order.interceptor.LoginInterceptor;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.order.vo.OrderConfirmVO;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.sms.vo.ItemSaleVO;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.vo.SkuLockVO;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private GmallOmsFeign gmallOmsFeign;
    @Autowired
    private AmqpTemplate amqpTemplate;

    private static final String ORDER_TOKEN_PREFIX = "order:token:";

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
            this.redisTemplate.opsForValue().set(ORDER_TOKEN_PREFIX + timeId, timeId);
        }, threadPool);

        CompletableFuture.allOf(addressFuture, cartFuture,
                boundFuture, orderTokenFuture).join();

        return orderConfirmVO;
    }

    @Override
    public OrderEntity submit(OrderSubmitVO submitVO) {

        //1.验证令牌防止重复提交
        String orderToken = submitVO.getOrderToken();
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        Long flag = this.redisTemplate.execute(redisScript, Collections.singletonList(ORDER_TOKEN_PREFIX + orderToken), orderToken);

        if (flag == 0L) {
            throw new RuntimeException("请不要重复提交！");
        }
        //2.验证价格
        BigDecimal totalPrice = submitVO.getTotalPrice();
        List<OrderItemVO> orderItemVOS = submitVO.getOrderItemVOS();
        if (CollectionUtils.isEmpty(orderItemVOS)) {
            throw new RuntimeException("请添加购物车清单！");
        }

        BigDecimal currentPrice = orderItemVOS.stream().map(orderItemVO -> {
            Resp<SkuInfoEntity> skuInfoEntityResp = this.gmallPmsFeign.querySkuinfoBySkuId(orderItemVO.getSkuId());
            SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
            return skuInfoEntity.getPrice().multiply(new BigDecimal(orderItemVO.getCount()));
        }).reduce(BigDecimal::add).get();//等价reduce((a, b) -> a.add(b)).get()

        if (totalPrice.compareTo(currentPrice) != 0) {
            throw new RuntimeException("请刷新页面后重试！");
        }

        //3.验证库存，并锁定库存
        List<SkuLockVO> skuLockVOS = orderItemVOS.stream().map(orderItemVO -> {
            SkuLockVO skuLockVO = new SkuLockVO();
            skuLockVO.setSkuId(orderItemVO.getSkuId());
            skuLockVO.setCount(orderItemVO.getCount());
            skuLockVO.setOrderToken(orderToken);
            return skuLockVO;
        }).collect(Collectors.toList());
        Resp<String> stringResp = this.gmallWmsFeign.checkAndLock(skuLockVOS);
        if (stringResp.getCode() == 1) {
            throw new RuntimeException(stringResp.getMsg());
        }

        //4.生成订单
        Resp<OrderEntity> orderEntityResp = null;
        UserInfo userInfo = LoginInterceptor.get();
        try {
            submitVO.setUserId(userInfo.getUserId());
            Resp<MemberEntity> memberEntityResp = this.gmallUmsFeign.queryUserById(userInfo.getUserId());
            MemberEntity memberEntity = memberEntityResp.getData();
            submitVO.setUserName(memberEntity.getUsername());

            orderEntityResp = this.gmallOmsFeign.creatOrder(submitVO);
        } catch (Exception e) {
            e.printStackTrace();
            //this.amqpTemplate.convertAndSend("WMS-EXCHANGE", "wms.ttl", orderToken);
            throw new RuntimeException("订单创建失败！服务器异常");
        }

        //5.删购物车对应的记录(消息队列)
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userInfo.getUserId());
        List<Long> skuIds = orderItemVOS.stream().map(OrderItemVO::getSkuId).collect(Collectors.toList());
        map.put("skuIds", skuIds);
        this.amqpTemplate.convertAndSend("GMALL-ORDER-EXCHANGE", "cart.delete", map);

        if (orderEntityResp != null) {
            return orderEntityResp.getData();
        }
        return null;
    }

    @Override
    public void paySuccess(String out_trade_no) {

        this.amqpTemplate.convertAndSend("GMALL-ORDER-EXCHANGE", "order.pay", out_trade_no);


    }
}
