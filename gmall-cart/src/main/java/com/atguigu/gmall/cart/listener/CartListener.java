package com.atguigu.gmall.cart.listener;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.feign.GmallPmsFeign;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    //设置redis中商品前缀
    private static final String CART_PREFIX = "cart:uid:";

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

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "GMALL-CART-REMOVE-QUEUE", durable = "true"),
            exchange = @Exchange(value = "GMALL-ORDER-EXCHANGE",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = {"cart.delete"}
    ))
    public void removeCartListener(Map<String, Object> map, Message message, Channel channel) throws IOException {

        String userId = map.get("userId").toString();
        List<Long> skuIds = (List<Long>) map.get("skuIds");
        List<String> skuIdString = skuIds.stream().map(Object::toString).collect(Collectors.toList());

        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(CART_PREFIX + userId);
        hashOps.delete(skuIdString.toArray());
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            e.printStackTrace();
        }

    }
}
