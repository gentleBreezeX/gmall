package com.atguigu.gmall.oms.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
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
        return new TopicExchange("OMS-EXCHANGE", true, false);
    }

    @Bean
    public Queue queue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "OMS-EXCHANGE");
        arguments.put("x-dead-letter-routing-key", "oms.dead");
        arguments.put("x-message-ttl", 110000); // 仅仅用于测试，实际根据需求，通常30分钟或者15分钟
        return new Queue("OMS-TTL-QUEUE", true, false, false, arguments);
    }

    @Bean
    public Binding binding() {
        return new Binding("OMS-TTL-QUEUE", Binding.DestinationType.QUEUE,
                "OMS-EXCHANGE", "oms.unlock", null);
    }

    @Bean
    public Queue deadQueue() {
        return new Queue("OMS-DEAD-QUEUE", true, false, false, null);
    }

    @Bean
    public Binding deadBinding() {
        return new Binding("OMS-DEAD-QUEUE", Binding.DestinationType.QUEUE,
                "OMS-EXCHANGE", "oms.dead", null);
    }











}
