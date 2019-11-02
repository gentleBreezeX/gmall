package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author breeze
 * @date 2019/10/31 13:52
 */
public class ProductAttrValueVO extends ProductAttrValueEntity {

    public void setValueSelected(List<Object> valueSelected){

        //如果接收的集合为空则返回
        if (CollectionUtils.isEmpty(valueSelected)) {
            return;
        }

        this.setAttrValue(StringUtils.join(valueSelected, ","));
    }
}
