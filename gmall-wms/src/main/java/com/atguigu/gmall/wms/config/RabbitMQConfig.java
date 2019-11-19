package com.atguigu.gmall.wms.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author breeze
 * @date 2019/11/18 18:08
 */
@Configuration
public class RabbitMQConfig {

    @Bean
    public Exchange exchange() {
        return new TopicExchange("WMS-EXCHANGE", true, false);
    }

    @Bean
    public Queue queue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "WMS-EXCHANGE");
        arguments.put("x-dead-letter-routing-key", "wms.ttl");
        arguments.put("x-message-ttl", 120000); // 仅仅用于测试，实际根据需求，通常30分钟或者15分钟
        return new Queue("WMS-TTL-QUEUE", true, false, false, arguments);
    }

    @Bean
    public Binding binding() {
        return new Binding("WMS-TTL-QUEUE", Binding.DestinationType.QUEUE,
                "WMS-EXCHANGE", "wms.unlock", null);
    }

    @Bean
    public Queue deadQueue() {
        return new Queue("WMS-DEAD-QUEUE", true, false, false, null);
    }

    @Bean
    public Binding deadBinding() {
        return new Binding("WMS-DEAD-QUEUE", Binding.DestinationType.QUEUE,
                "WMS-EXCHANGE", "wms.ttl", null);
    }











}
