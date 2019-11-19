package com.atguigu.gmall.oms.listener;

import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.service.OrderService;
import com.atguigu.gmall.ums.vo.UserBoundVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author breeze
 * @date 2019/11/18 19:39
 */
@Component
public class OmsListener {

    @Autowired
    private OrderService orderService;
    @Autowired
    private AmqpTemplate amqpTemplate;

    @RabbitListener(queues = {"OMS-DEAD-QUEUE"})
    public void closeOrderListener(String orderToken){

        //修改订单状态为已关闭
        if (this.orderService.closeOrder(orderToken) == 1) {

            //发送消息给库存，解锁库存
            this.amqpTemplate.convertAndSend("WMS-EXCHANGE", "wms.ttl", orderToken);

        }

    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "ORDER-SUCCESS-QUEUE", durable = "true"),
            exchange = @Exchange(value = "GMALL-ORDER-EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = {"order.pay"}
    ))
    public void success(String orderToken) {

        //更新订单状态
        if (this.orderService.success(orderToken) == 1) {
            //减库存
            this.amqpTemplate.convertAndSend("WMS-EXCHANGE", "stock.minus", orderToken);

            UserBoundVO userBoundVO = new UserBoundVO();

            OrderEntity orderEntity = this.orderService.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderToken));

            userBoundVO.setUserId(orderEntity.getMemberId());
            userBoundVO.setGrowth(orderEntity.getGrowth());
            userBoundVO.setIntegration(orderEntity.getIntegration());
            //加积分
            this.amqpTemplate.convertAndSend("UMS-EXCHANGE", "user.bound", userBoundVO);
        }

    }


}
