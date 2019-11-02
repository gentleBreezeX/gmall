package com.atguigu.gmall.pms.vo;


import com.atguigu.gmall.pms.entity.AttrEntity;
import io.swagger.annotations.Api;
import lombok.Data;

/**
 * @author breeze
 * @date 2019/10/31 9:46
 */
@Api("保存规格参数")
@Data
public class AttrVO extends AttrEntity {

    private Long attrGroupId;
}
