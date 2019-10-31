package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.AttrGroupRelationVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 属性分组
 *
 * @author breeze
 * @email gentle@breeze.com
 * @date 2019-10-28 18:25:21
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageVo queryPage(QueryCondition params);

    PageVo getAttrGroupByCidWithPage(Long cid, QueryCondition condition);

    AttrGroupRelationVO getAttrWithGroupByGid(Long gid);
}

