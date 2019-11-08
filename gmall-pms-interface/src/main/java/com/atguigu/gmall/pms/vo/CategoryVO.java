package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import lombok.Data;

import java.util.List;

/**
 * @author breeze
 * @date 2019/11/8 18:43
 */
@Data
public class CategoryVO extends CategoryEntity {

    private List<CategoryEntity> subs;
}
