package com.atguigu.gmall.ums.controller;

import java.util.*;
import java.util.concurrent.TimeUnit;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.core.consts.Consts;
import com.atguigu.core.utils.CommonUtils;
import com.atguigu.gmall.ums.template.SmsTemplate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.service.MemberService;




/**
 * 会员
 *
 * @author breeze
 * @email gentle@breeze.com
 * @date 2019-10-28 18:27:30
 */
@Api(tags = "会员 管理")
@RestController
@RequestMapping("ums/member")
public class MemberController {

    @Autowired
    private MemberService memberService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private SmsTemplate smsTemplate;

    @ApiOperation("根据用户名和密码查询用户")
    @GetMapping("query")
    public Resp<MemberEntity> queryUserByUsernameAndPassword(@RequestParam("username")String username,
                                                       @RequestParam("password")String password){

        MemberEntity memberEntity = this.memberService.queryUser(username, password);

        return Resp.ok(memberEntity);
    }

    @ApiOperation("处理用户注册")
    @PostMapping("register")
    public Resp<Object> register(MemberEntity memberEntity, @RequestParam("code")String code){

        this.memberService.register(memberEntity, code);

        return Resp.ok(null);
    }

    @ApiOperation("发送验证码")
    @PostMapping("sendSms")
    public Resp<String> sendSms(@RequestParam("mobile")String mobile){

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


    @ApiOperation("验证账号邮箱手机是否可用")
    @GetMapping("check/{data}/{type}")
    public Resp<Boolean> checkData(@PathVariable("data")String data, @PathVariable("type")Integer type){

        Boolean b = this.memberService.checkData(data, type);
        return Resp.ok(b);
    }

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ums:member:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = memberService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('ums:member:info')")
    public Resp<MemberEntity> info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return Resp.ok(member);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('ums:member:save')")
    public Resp<Object> save(@RequestBody MemberEntity member){
		memberService.save(member);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('ums:member:update')")
    public Resp<Object> update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('ums:member:delete')")
    public Resp<Object> delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}
