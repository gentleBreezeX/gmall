package com.atguigu.gmall.message.listener;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.consts.Consts;
import com.atguigu.core.utils.CommonUtils;
import com.atguigu.gmall.message.config.SmsTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author breeze
 * @date 2019/11/12 20:50
 */
@Component
public class SMSListener {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private SmsTemplate smsTemplate;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "GMALL-MESSAGE-QUEUE", durable = "true"),
            exchange = @Exchange(value = "gmall.message.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = {"message.*"}
    ))
    public void listenerCode(Map<String, Object> map){
        //手机号
        String mobile = (String)map.get("mobile");
        Integer type = (Integer)map.get("type");//type 1:验证码
        String code = (String)map.get("code");
        String tplId = null;//发送短信的类型
        switch (type) {
            case 1: tplId = "TP1711063"; break;
            default: break;
        }

        Map<String, String> querys = new HashMap<>(3);
        querys.put("mobile", mobile);
        querys.put("param", "code:" + code);
        querys.put("tpl_id", tplId);
        boolean sendCode = smsTemplate.sendCode(querys);
        //短信发送失败直接返回
        if (!sendCode) {
            return;
        }

    }
}
