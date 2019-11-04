package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.sms.dao.SkuFullReductionDao;
import com.atguigu.gmall.sms.dao.SkuLadderDao;
import com.atguigu.gmall.sms.dto.SkuSaleDTO;
import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.sms.dao.SkuBoundsDao;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.service.SkuBoundsService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("skuBoundsService")
public class SkuBoundsServiceImpl extends ServiceImpl<SkuBoundsDao, SkuBoundsEntity> implements SkuBoundsService {

    @Autowired
    private SkuFullReductionDao skuFullReductionDao;
    @Autowired
    private SkuLadderDao skuLadderDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SkuBoundsEntity> page = this.page(
                new Query<SkuBoundsEntity>().getPage(params),
                new QueryWrapper<SkuBoundsEntity>()
        );

        return new PageVo(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSkuSaleInfo(SkuSaleDTO skuSaleDTO) {

        //3.新增营销相关3张 skuId
        //3.1 新增积分skuBounds
        SkuBoundsEntity skuBoundsEntity = new SkuBoundsEntity();
        BeanUtils.copyProperties(skuSaleDTO, skuBoundsEntity);

        List<Integer> work = skuSaleDTO.getWork();

        if (CollectionUtils.isEmpty(work)) {
            skuBoundsEntity.setWork(work.get(0)*8 + work.get(1)*4 + work.get(2)*2 + work.get(3));
        }
        this.save(skuBoundsEntity);

        //3.2 新增打折信息：skuLadder
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuSaleDTO,skuFullReductionEntity);

        skuFullReductionEntity.setAddOther(skuSaleDTO.getFullAddOther());
        this.skuFullReductionDao.insert(skuFullReductionEntity);

        //3.3 新增skuReduction
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(skuSaleDTO,skuLadderEntity);

        skuLadderEntity.setAddOther(skuSaleDTO.getLadderAddOther());
        this.skuLadderDao.insert(skuLadderEntity);
    }

}