package com.atguigu.gmall.index.service;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVO;

import java.util.List;

/**
 * @author breeze
 * @date 2019/11/8 18:04
 */
public interface IndexService {
    List<CategoryEntity> listParentCates();

    List<CategoryVO> listChildrenCate(Long pid);

    String testThread();

    String test();

    String testRedisson();
}
