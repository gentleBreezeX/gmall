package com.atguigu.gmall.index.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.annotation.CategoryCache;
import com.atguigu.gmall.index.feign.GmallPmsFeign;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.consts.RedisConsts;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVO;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author breeze
 * @date 2019/11/8 18:04
 */
@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private GmallPmsFeign gmallPmsFeign;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public List<CategoryEntity> listParentCates() {

        Resp<List<CategoryEntity>> listResp = gmallPmsFeign.listCategory(1, 0L);

        return listResp.getData();
    }

    @CategoryCache(timeout = 7, random = 6, unit = TimeUnit.DAYS)
    @Override
    public List<CategoryVO> listSubCate(Long pid) {

        //2. 如果redis中没有，查询数据库
        Resp<List<CategoryVO>> listResp = gmallPmsFeign.listChildrenCate(pid);
        List<CategoryVO> categoryVOS = listResp.getData();

        return categoryVOS;
    }

    @Override
    public List<CategoryVO> listChildrenCate(Long pid) {

        //1. 查询redis缓存，缓存中有的话直接返回
        String categoryCache = this.redisTemplate.opsForValue().get(RedisConsts.PREFIX_CATEGORY + pid);
        if (StringUtils.isNotBlank(categoryCache)) {
            return JSON.parseArray(categoryCache, CategoryVO.class);
        }

        //2. 如果redis中没有，查询数据库
        Resp<List<CategoryVO>> listResp = gmallPmsFeign.listChildrenCate(pid);
        List<CategoryVO> categoryVOS = listResp.getData();

        /**
         * 3. 将结果放入redis缓存
         *      防止雪崩: 缓存时间设置为随机值(7+n)
         *      防止穿透(恶意攻击)：null值也存入
         *      防止击穿：分布式锁
         */
        int addDay = new Random().nextInt(6);
        redisTemplate.opsForValue().set(
                RedisConsts.PREFIX_CATEGORY + pid, JSON.toJSONString(categoryVOS),
                RedisConsts.SAVE_CATEGORY_DAY+addDay, TimeUnit.DAYS);

        return categoryVOS;
    }

    /**
     * synchronized无法解决分布式的线程安全问题
     * @return
     */
    @Override
    public synchronized String testThread() {

        String numString = redisTemplate.opsForValue().get("num");
        if (StringUtils.isNotBlank(numString)) {
            int num = Integer.parseInt(numString);
            redisTemplate.opsForValue().set("num", String.valueOf(++num));
        }else {
            int num = 0;
            redisTemplate.opsForValue().set("num", String.valueOf(num));
        }
        return "添加成功";
    }

    /**
     * 手写redis分布式锁
     *      1. 如果获取锁之后，服务器宕机，就会出现死锁 --> 设置超时时间解决
     *      2. 假设业务时间超过10s，就会出现业务没有执行完毕就释放了，可能把第二个线程的锁删掉
     *              --->  删锁之前判断是不是自己的锁
     *      3. 删锁判断，依然会引起线程1以及线程2存在一段时间裸奔，可以使用redisson
     *      4. 释放锁的if判断还是会出现原子性的问题，可以使用lua脚步优化
     * @return
     */
    @Override
    public String test() {
        //所有请求竞争锁  ---> 如果不存在lock 就设置redis键值
        String uuid = UUID.randomUUID().toString();
        Boolean lock = this.redisTemplate.opsForValue().setIfAbsent("lock", uuid, 10L, TimeUnit.SECONDS);
        if (lock) {
            //操作redis，num++
            String numString = redisTemplate.opsForValue().get("num");
            if (StringUtils.isNotBlank(numString)) {
                int num = Integer.parseInt(numString);
                redisTemplate.opsForValue().set("num", String.valueOf(++num));
            } else {
                int num = 0;
                redisTemplate.opsForValue().set("num", String.valueOf(num));
            }

            //释放锁
            //lua脚本
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
            this.redisTemplate.execute(redisScript, Collections.singletonList("lock"), uuid);
//            if (StringUtils.equals(uuid, this.redisTemplate.opsForValue().get("lock"))) {
//
//                this.redisTemplate.delete("lock");
//            }
        } else {
            //没有抢到锁进行重试机制
            //暂停一会儿(秒)
            try {TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) {e.printStackTrace(); }
            test();
        }
        return "添加成功";
    }

    /**
     * 测试Redisson框架的分布式锁
     * @return
     */
    @Override
    public String testRedisson() {
        //获取锁
        RLock lock = this.redissonClient.getLock("lock");
        lock.lock();

        //操作redis，num++
        String numString = redisTemplate.opsForValue().get("num");
        if (StringUtils.isNotBlank(numString)) {
            int num = Integer.parseInt(numString);
            redisTemplate.opsForValue().set("num", String.valueOf(++num));
        } else {
            int num = 0;
            redisTemplate.opsForValue().set("num", String.valueOf(num));
        }

        //释放锁
        lock.unlock();

        return "添加成功";
    }


}
