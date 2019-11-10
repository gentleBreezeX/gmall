package com.atguigu.gmall.index.aspect;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.index.annotation.CategoryCache;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author breeze
 * @date 2019/11/9 16:25
 */
@Aspect
@Component
public class CategoryCacheAspect {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    /**
     * AOP环绕通知必须的四个要素：
     * joinPoint.proceed(args)执行业务方法
     * @param joinPoint 形参必须是这个
     * @return 返回值必须是object
     * @throws Throwable 必须抛出这个异常
     */
    @Around("@annotation(com.atguigu.gmall.index.annotation.CategoryCache)")
    public Object cacheAroudAdvice(ProceedingJoinPoint joinPoint) throws Throwable{

        Object result = null;
        //获取连接点签名
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        //获取连接点的注解信息
        CategoryCache categoryCache = signature.getMethod().getAnnotation(CategoryCache.class);
        //获取原方法的返回值类型
        Class returnType = signature.getReturnType();
        //获取注解参数
        String prefix = categoryCache.prefix();
        TimeUnit unit = categoryCache.unit();
        long timeout = categoryCache.timeout();
        long random = categoryCache.random();
        //获取方法的参数
        Object[] pid = joinPoint.getArgs();
        String pidString = Arrays.asList(pid).toString();
        //设置缓存的key
        String key = prefix + pidString;

        //1. 判断redis中是否存在菜单缓存
        result = cacheHit(key, returnType);
        if (result != null) {
            return result;
        }

        //3. 不存在的话，添加分布式锁
        RLock lock = this.redissonClient.getLock("cateLock" + pidString);
        lock.lock();

        //4. 再次查询redis中是否存在缓存(DCL)
        result = cacheHit(key, returnType);
        if (result != null) {
            //4.1 命中缓存需要释放锁
            lock.unlock();
            return result;
        }

        //5. 执行查询的业务逻辑从数据库查询
        result = joinPoint.proceed(pid);

        //6. 将数据查询数据存储到redis缓存中
        this.redisTemplate.opsForValue().set(key, JSON.toJSONString(result),
                timeout + new Random().nextInt((int) random), unit);

        //7. 释放锁
        lock.unlock();
        //8. 返回查询数据
        return result;
    }

    /**
     * 查询缓存方法提取
     * @param key
     * @param returnType
     * @return
     */
    private Object cacheHit(String key, Class returnType){
        String catesCache = this.redisTemplate.opsForValue().get(key);
        //2. 存在直接返回
        if (StringUtils.isNotBlank(catesCache)) {
            // 不能使用parseArray<cache, T>，因为不知道List<T>中的泛型
            return JSON.parseObject(catesCache, returnType);
        }
        return null;
    }
}
