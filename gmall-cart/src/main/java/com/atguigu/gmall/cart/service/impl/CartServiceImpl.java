package com.atguigu.gmall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.feign.GmallPmsFeign;
import com.atguigu.gmall.cart.feign.GmallSmsFeign;
import com.atguigu.gmall.cart.interceptor.LoginInterceptor;
import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.cart.vo.Cart;
import com.atguigu.gmall.cart.vo.UserInfo;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.sms.vo.ItemSaleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author breeze
 * @date 2019/11/13 16:11
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private GmallPmsFeign pmsFeign;
    @Autowired
    private GmallSmsFeign smsFeign;
    @Autowired
    private StringRedisTemplate redisTemplate;
    //设置redis中商品前缀
    private static final String CART_PREFIX = "cart:uid:";
    //redis商品最新价格的前缀
    private static final String CURRENT_PRICE_PREFIX = "cart:price:";

    @Override
    public void addCart(Cart cart) {
        //获取UserInfo中userKey和userId
        String key = getKey();

        //查询redis中购物车是否存在
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);

        Long skuId = cart.getSkuId();
        Integer count = cart.getCount();

        //注意skuId要转换成String类型，因为redis中都是String
        if (hashOps.hasKey(skuId.toString())) {
            // 购物车已存在该记录，更新数量
            String cartJson = hashOps.get(skuId.toString()).toString();
            cart = JSON.parseObject(cartJson, Cart.class);
            cart.setCount(cart.getCount() + count);
        } else {
            //购物车不存在
            //给cart添加属性
            Resp<SkuInfoEntity> skuInfoEntityResp = this.pmsFeign.querySkuinfoBySkuId(skuId);
            SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
            cart.setDefaultImage(skuInfoEntity.getSkuDefaultImg());
            cart.setPrice(skuInfoEntity.getPrice());
            cart.setSkuTitle(skuInfoEntity.getSkuTitle());
            cart.setCheck(true);

            Resp<List<SkuSaleAttrValueEntity>> skuSaleAttrResp = this.pmsFeign.querySkuSaleAttrBySkuId(skuId);
            cart.setSkuAttrValue(skuSaleAttrResp.getData());

            Resp<List<ItemSaleVO>> itemSaleResp = this.smsFeign.queryItemSaleVOs(skuId);
            cart.setSales(itemSaleResp.getData());
            //把skuId对应商品的当前价格存入redis
            this.redisTemplate.opsForValue().set(CURRENT_PRICE_PREFIX + skuId, skuInfoEntity.getPrice().toString());
        }
        //同步到redis中
        hashOps.put(skuId.toString(), JSON.toJSONString(cart));

    }

    @Override
    public List<Cart> queryCarts() {
        //获取UserInfo中userKey和userId
        UserInfo userInfo = LoginInterceptor.get();
        String userKey = userInfo.getUserKey();
        Long userId = userInfo.getUserId();

        //登录的用户需要合并未登录的购物车, 所以无论怎样都需要先查询未登录的购物车
        List<Cart> userKeyCarts = null;
        String usrKey = CART_PREFIX + userKey;

        //查询redis中购物车
        BoundHashOperations<String, Object, Object> userKeyOps = this.redisTemplate.boundHashOps(usrKey);
        List<Object> userKeyCartsJsonList = userKeyOps.values();

        //如果未登录的购物车不为空
        if (!CollectionUtils.isEmpty(userKeyCartsJsonList)) {
            userKeyCarts = userKeyCartsJsonList.stream().map(o -> {
                Cart cart = JSON.parseObject(o.toString(), Cart.class);
                //获取redis中当前价格设置进未登录的购物车
                cart.setCurrentPrice(new BigDecimal(this.redisTemplate.opsForValue().get(CURRENT_PRICE_PREFIX + cart.getSkuId())));
                return cart;
            }).collect(Collectors.toList());
        }

        //判断用户是否登录
        if (userId == null || userId == 0) {
            return userKeyCarts;
        }

        //用户已登录, 查询登录状态的购物车
        String userIdKey = CART_PREFIX + userId;
        BoundHashOperations<String, Object, Object> userIdOps = this.redisTemplate.boundHashOps(userIdKey);

        //如果未登录状态的购物车不为空，就需要合并
        if (!CollectionUtils.isEmpty(userKeyCarts)) {
            //合并购物车
            userKeyCarts.forEach(userKeyCart -> {

                Long skuId = userKeyCart.getSkuId();
                Integer count = userKeyCart.getCount();
                //判断登录购物车中是否有相同的商品
                if (userIdOps.hasKey(skuId.toString())) {
                    //购物车中存在相同的商品，更新商品
                    String cartJson = userIdOps.get(skuId.toString()).toString();
                    Cart idCart  = JSON.parseObject(cartJson, Cart.class);
                    idCart.setCount(idCart.getCount() + count);
                    userIdOps.put(skuId.toString(), JSON.toJSONString(idCart));
                }else {
                    //购物车中不存在该商品就新增
                    userIdOps.put(skuId.toString(), JSON.toJSONString(userKeyCart));
                }
            });
            //合并后删除未登录的购物车
            this.redisTemplate.delete(usrKey);
        }

        //返回登录状态的购物车
        List<Object> userIdCartsJsonList = userIdOps.values();
        if (!CollectionUtils.isEmpty(userIdCartsJsonList)) {
            return userIdCartsJsonList.stream().map(userIdCartJson -> {
                Cart cart = JSON.parseObject(userIdCartJson.toString(), Cart.class);
                cart.setCurrentPrice(new BigDecimal(this.redisTemplate.opsForValue().get(CURRENT_PRICE_PREFIX + cart.getSkuId())));
                return cart;
            }).collect(Collectors.toList());
        }

        return null;
    }

    @Override
    public void updateCart(Cart cart) {

        //获取key
        String key = getKey();

        Integer count = cart.getCount();

        //查询redis中购物车
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);

        String skuId = cart.getSkuId().toString();
        if (hashOps.hasKey(skuId)) {
            //如果存在获取购物车中记录
            String cartJson = hashOps.get(skuId).toString();
            cart = JSON.parseObject(cartJson, Cart.class);
            cart.setCount(count);
            hashOps.put(skuId, JSON.toJSONString(cart));
        }
    }

    @Override
    public void removeCart(Long skuId) {
        String key = getKey();

        //查询redis中购物车
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);

        if (hashOps.hasKey(skuId.toString())) {
            hashOps.delete(skuId.toString());
        }
    }



    @Override
    public void checkCart(List<Cart> carts) {
        //获取key
        String key = getKey();

        //查询redis中购物车
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);

        carts.forEach(cart -> {
            Boolean check = cart.getCheck();
            String skuId = cart.getSkuId().toString();
            if (hashOps.hasKey(skuId)) {
                //如果存在获取购物车中记录
                String cartJson = hashOps.get(skuId).toString();
                cart = JSON.parseObject(cartJson, Cart.class);
                cart.setCheck(check);
                hashOps.put(skuId, JSON.toJSONString(cart));
            }
        });


    }

    private String getKey() {
        //获取UserInfo中userKey和userId
        UserInfo userInfo = LoginInterceptor.get();
        String userKey = userInfo.getUserKey();
        Long userId = userInfo.getUserId();

        //获取redis中key
        String key = CART_PREFIX;
        if (userInfo.getUserId() == null || userInfo.getUserId() == 0) {
            key += userKey;
        } else {
            key += userId;
        }
        return key;
    }

}
