package com.atguigu.gmall.message.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.consts.Consts;
import com.atguigu.core.utils.CommonUtils;
import com.atguigu.gmall.message.config.SmsTemplate;
import com.atguigu.gmall.message.service.MessageService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author breeze
 * @date 2019/11/12 20:03
 */
@RestController
@RequestMapping("message")
public class MessageController {

    /*@Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private SmsTemplate smsTemplate;

    @ApiOperation("发送验证码")
    @PostMapping("register/code")
    public Resp<String> sendRegisterCode(@RequestParam("mobile")String mobile){

        //1. 验证手机号码是否正确
        boolean mobilePhone = CommonUtils.isMobilePhone(mobile);
        if (!mobilePhone) {
            return Resp.fail("您输入的手机号码不正确");
        }
        //2.验证redis中存储的当前手机号获取验证码的次数
        //第一次获取没有，或者没有超过指定次数可以继续获取验证码
        //一个手机号码一天内最多获取五次验证码
        String countStr = this.redisTemplate.opsForValue().get(Consts.CODE_COUNT_PREFIX + mobile);
        int count = 0;
        //如果redis中记录次数不为空
        if (StringUtils.isNotBlank(countStr)) {
            count = Integer.parseInt(countStr);
        }

        if (count > Consts.CODE_COUNT){
            return Resp.fail("验证码获取次数超出限制");
        }

        //验证redis中当前手机号码是否存在未过期的验证码
        Boolean hasKey = redisTemplate.hasKey(Consts.CODE_PREFIX + mobile);
        if (hasKey) {
            return Resp.fail("请不要频繁获取验证码");
        }

        //发送验证码
        String code = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        Map<String, String> querys = new HashMap<>();
        querys.put("mobile", mobile);
        querys.put("param", "code:" + code);
        querys.put("tpl_id", "TP1711063");
        boolean sendCode = smsTemplate.sendCode(querys);
        if (!sendCode) {
            return Resp.fail("短信验证码发送失败");
        }
        //将验证码存在redis中15分钟
        redisTemplate.opsForValue().set(Consts.CODE_PREFIX + mobile,
                code, 15, TimeUnit.MINUTES);
        //修改该手机的验证码的次数
        Long expire = redisTemplate.getExpire(Consts.CODE_COUNT_PREFIX + mobile, TimeUnit.MINUTES);
        if (expire == null || expire <= 0) {
            expire = (long)(24*60);
        }
        //存入一个获取验证码的次数
        count++;
        redisTemplate.opsForValue().set(Consts.CODE_COUNT_PREFIX + mobile,
                count+"", expire, TimeUnit.MINUTES);

        return Resp.ok("发送成功");
    }
*/
}
