package com.atguigu.gmall.pms.vo;

import lombok.Data;

/**
 * @author breeze
 * @date 2019/11/5 22:33
 */
@Data
public class SpuAttributeValueVO {

    //当前sku对应的属性的attr_id
    private Long productAttributeId;
    //属性名  电池
    private String name;
    //3G   3000mah
    private String value;
}
