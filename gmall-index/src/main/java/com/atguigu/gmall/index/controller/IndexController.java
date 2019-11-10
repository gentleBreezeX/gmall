package com.atguigu.gmall.index.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author breeze
 * @date 2019/11/8 17:52
 */
@RestController
@RequestMapping("/index")
public class IndexController {

    @Autowired
    private IndexService indexService;

    @GetMapping("/testRedisson")
    @ApiOperation("测试分布式锁框架Redisson")
    public Resp<Object> testRedisson(){

        String msg = indexService.testRedisson();

        return Resp.ok(msg);
    }

    @GetMapping("/test1")
    @ApiOperation("测试分布式锁高并发添加redis数据")
    public Resp<Object> test(){

        String msg = indexService.test();

        return Resp.ok(msg);
    }

    @GetMapping("/test")
    @ApiOperation("测试高并发添加redis数据")
    public Resp<Object> testThread(){

        String msg = indexService.testThread();

        return Resp.ok(msg);
    }



    @GetMapping("/cates")
    @ApiOperation("响应门户页面一级菜单")
    public Resp<List<CategoryEntity>> listParentCate(){

        List<CategoryEntity> categoryEntities = indexService.listParentCates();

        return Resp.ok(categoryEntities);
    }

    @GetMapping("/cates/{pid}")
    @ApiOperation("响应门户页面二三级菜单")
    public Resp<List<CategoryVO>> listChildrenCate(@PathVariable("pid")Long pid){

        /**
         * 使用分布式锁+自定义注解+AOP实现查询菜单
         */
        List<CategoryVO> categoryVOS = indexService.listSubCate(pid);

        /**
         * 这个是常规的实现redis缓存
         * List<CategoryVO> categoryVOS = indexService.listChildrenCate(pid);
         */


        return Resp.ok(categoryVOS);
    }
}
