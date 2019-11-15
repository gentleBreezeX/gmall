package com.atguigu.gmall.cart.listener;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.feign.GmallPmsFeign;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author breeze
 * @date 2019/11/15 18:06
 */
@Component
public class CartListener {

    @Autowired
    private GmallPmsFeign gmallPmsFeign;
    @Autowired
    private StringRedisTemplate redisTemplate;
    //redis商品最新价格的前缀
    private static final String CURRENT_PRICE_PREFIX = "cart:price:";

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "GMALL-CART-QUEUE", durable = "true"),
            exchange = @Exchange(value = "gmall.item.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = {"item.update"}
    ))
    public void listener(Map<String, Object> map){
        Long spuId = (Long)map.get("id");

        Resp<List<SkuInfoEntity>> skuInfoResp = this.gmallPmsFeign.listSkuInfo(spuId);
        List<SkuInfoEntity> skuInfoEntities = skuInfoResp.getData();
        skuInfoEntities.forEach(skuInfoEntity -> {

            this.redisTemplate.opsForValue()
                    .set(CURRENT_PRICE_PREFIX + skuInfoEntity.getSkuId(), skuInfoEntity.getPrice().toString());

        });
    }
}
