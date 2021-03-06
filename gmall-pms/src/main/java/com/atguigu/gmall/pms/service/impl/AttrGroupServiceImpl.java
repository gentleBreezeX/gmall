package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.AttrAttrgroupRelationDao;
import com.atguigu.gmall.pms.dao.AttrDao;
import com.atguigu.gmall.pms.dao.ProductAttrValueDao;
import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import com.atguigu.gmall.pms.vo.AttrGroupRelationVO;
import com.atguigu.gmall.pms.vo.GroupVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.AttrGroupDao;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.service.AttrGroupService;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrDao attrDao;
    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Autowired
    private AttrGroupDao attrGroupDao;
    @Autowired
    private ProductAttrValueDao productAttrValueDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo getAttrGroupByCidWithPage(Long cid, QueryCondition condition) {

        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(condition),
                new QueryWrapper<AttrGroupEntity>().eq("catelog_id", cid)
        );

        return new PageVo(page);
    }

    @Override
    public AttrGroupRelationVO getAttrWithGroupByGid(Long gid) {

        AttrGroupRelationVO attrGroupRelationVO = new AttrGroupRelationVO();

        //查询分组
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(gid);
        BeanUtils.copyProperties(attrGroupEntity, attrGroupRelationVO);

        //根据gid查询关联表（即attr和attrGroup的关联表）
        List<AttrAttrgroupRelationEntity> relations = attrAttrgroupRelationDao.selectList(
                new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", gid));

        if (CollectionUtils.isEmpty(relations)){
            return attrGroupRelationVO;
        }
        attrGroupRelationVO.setRelations(relations);

        //relations数据回显得到attr_id
        List<Long> attrIds = relations.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
        List<AttrEntity> attrEntities = attrDao.selectBatchIds(attrIds);
        attrGroupRelationVO.setAttrEntities(attrEntities);

        return attrGroupRelationVO;
    }

    @Override
    public List<AttrGroupRelationVO> listAttrAndGroup(Long cid) {

        AttrGroupRelationVO relationVO = new AttrGroupRelationVO();

        List<AttrGroupEntity> attrGroupEntities = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", cid));

        List<AttrGroupRelationVO> relationVOS = attrGroupEntities.stream().map(attrGroupEntity ->
                this.getAttrWithGroupByGid(attrGroupEntity.getAttrGroupId()))
                .collect(Collectors.toList());

        return relationVOS;
    }

    @Override
    public List<GroupVO> queryGroupVOByCid(Long cid, Long spuId) {

        List<AttrGroupEntity> groupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", cid));
        if (CollectionUtils.isEmpty(groupEntities)) {
            return null;
        }

        return groupEntities.stream().map(attrGroupEntity -> {
            GroupVO groupVO = new GroupVO();
            groupVO.setGroupName(attrGroupEntity.getAttrGroupName());

            List<ProductAttrValueEntity> attrValueEntities = this.productAttrValueDao.queryByGidAndSpuId(spuId, attrGroupEntity.getAttrGroupId());
            groupVO.setBaseAttrValues(attrValueEntities);

            return groupVO;

        }).collect(Collectors.toList());

    }

}