package com.atguigu.gmall.ums.service.impl;

import com.atguigu.core.consts.Consts;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.ums.dao.MemberDao;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageVo(page);
    }

    @Override
    public Boolean checkData(String data, Integer type) {

        QueryWrapper<MemberEntity> wrapper = new QueryWrapper<>();
        switch (type) {
            case 1: wrapper.eq("username", data); break;
            case 2: wrapper.eq("mobile", data); break;
            case 3: wrapper.eq("email", data); break;
            default: return null;
        }

        return this.count(wrapper) == 0;
    }

    @Override
    public void register(MemberEntity memberEntity, String code) {
        //1. 验证验证码
        String codeRedis = this.redisTemplate.opsForValue().get(Consts.CODE_PREFIX + memberEntity.getMobile());

        if (!StringUtils.equals(code, codeRedis)) {
            throw new IllegalArgumentException("验证码错误");
        }

        //2. 生成盐
        String salt = UUID.randomUUID().toString().replace("-", "");
        memberEntity.setSalt(salt);

        //3. 对密码加密
        memberEntity.setPassword(DigestUtils.md5Hex(salt
                + DigestUtils.md5Hex(memberEntity.getPassword())));

        //4. 设置注册时间等其他时间
        memberEntity.setCreateTime(new Date());

        //5. 保存到数据库
        boolean b = this.save(memberEntity);

        //6. 注册成功，删除redis中验证码
        if (b) {
            this.redisTemplate.delete(Consts.CODE_PREFIX + memberEntity.getMobile());
        }
    }

    @Override
    public MemberEntity queryUser(String username, String password) {

        // 查询
        MemberEntity memberEntity = this.getOne(new QueryWrapper<MemberEntity>().eq("username", username));
        // 校验用户名
        if (memberEntity == null) {
            throw new IllegalArgumentException("账号或密码不正确");
        }
        // 校验密码
        if (!memberEntity.getPassword().equals(DigestUtils.md5Hex(memberEntity.getSalt() + DigestUtils.md5Hex(password)))) {
            throw new IllegalArgumentException("账号或密码不正确");
        }
        // 用户名密码都正确
        return memberEntity;
    }
}