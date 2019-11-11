package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.sms.dao.SkuFullReductionDao;
import com.atguigu.gmall.sms.dao.SkuLadderDao;
import com.atguigu.gmall.sms.dto.SkuSaleDTO;
import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import com.atguigu.gmall.sms.vo.ItemSaleVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
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
public class
SkuBoundsServiceImpl extends ServiceImpl<SkuBoundsDao, SkuBoundsEntity> implements SkuBoundsService {

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

    @Override
    public List<ItemSaleVO> queryItemSaleVOs(Long skuId) {

        List<ItemSaleVO> itemSaleVOS = new ArrayList<>();

        //查询积分信息
        List<SkuBoundsEntity> skuBoundsEntities = this.list(new QueryWrapper<SkuBoundsEntity>().eq("sku_id", skuId));
        if (!CollectionUtils.isEmpty(skuBoundsEntities)) {
            ItemSaleVO itemSaleVO = new ItemSaleVO();
            itemSaleVO.setType("积分");
            BigDecimal buyBounds = skuBoundsEntities.get(0).getBuyBounds();
            BigDecimal growBounds = skuBoundsEntities.get(0).getGrowBounds();
            itemSaleVO.setDesc("购物积分赠送：" + buyBounds.intValue() +
                    ", 成长积分赠送：" + growBounds.intValue());

            itemSaleVOS.add(itemSaleVO);
        }
        //查询满减信息
        List<SkuFullReductionEntity> fullReductionEntities = this.skuFullReductionDao.selectList(new QueryWrapper<SkuFullReductionEntity>().eq("sku_id", skuId));
        if (!CollectionUtils.isEmpty(fullReductionEntities)) {
            ItemSaleVO itemSaleVO = new ItemSaleVO();
            itemSaleVO.setType("满减");
            BigDecimal fullPrice = fullReductionEntities.get(0).getFullPrice();
            BigDecimal reducePrice = fullReductionEntities.get(0).getReducePrice();
            itemSaleVO.setDesc("满" + fullPrice.intValue() +
                    ", 减" + reducePrice.intValue());

            itemSaleVOS.add(itemSaleVO);
        }


        //查询打折信息
        List<SkuLadderEntity> ladderEntities = this.skuLadderDao.selectList(new QueryWrapper<SkuLadderEntity>().eq("sku_id", skuId));
        if (!CollectionUtils.isEmpty(ladderEntities)) {
            ItemSaleVO itemSaleVO = new ItemSaleVO();
            itemSaleVO.setType("打折");
            Integer fullCount = ladderEntities.get(0).getFullCount();
            BigDecimal discount = ladderEntities.get(0).getDiscount();
            itemSaleVO.setDesc("满" + fullCount.intValue() +
                    "件打" + discount.divide(new BigDecimal(10)).floatValue() + "折");

            itemSaleVOS.add(itemSaleVO);
        }

        return itemSaleVOS;
    }

}