package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.*;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.feign.SkuSaleFeign;
import com.atguigu.gmall.pms.service.ProductAttrValueService;
import com.atguigu.gmall.pms.service.SkuImagesService;
import com.atguigu.gmall.pms.service.SkuSaleAttrValueService;
import com.atguigu.gmall.pms.vo.ProductAttrValueVO;
import com.atguigu.gmall.pms.vo.SkuInfoVO;
import com.atguigu.gmall.pms.vo.SpuInfoVO;
import com.atguigu.gmall.sms.dto.SkuSaleDTO;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.service.SpuInfoService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDao spuInfoDao;
    @Autowired
    private SpuInfoDescDao spuInfoDescDao;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private SkuInfoDao skuInfoDao;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private AttrDao attrDao;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private SkuSaleFeign skuSaleFeign;


    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo listSpuInfo(Long catId, QueryCondition condition) {
        //封装分页条件
        IPage<SpuInfoEntity> page = new Query<SpuInfoEntity>().getPage(condition);
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        //如果catid不为0， 则根据catId查找
        if (catId != 0) {
            wrapper.eq("catalog_id",catId);
        }
        //获取查询的条件
        String key = condition.getKey();
        //判断字符串不能为空 空串 长度不为0
        if (StringUtils.isNotBlank(key)){
            wrapper.and(t -> t.like("spu_name", key)).or().like("id", key);
        }

        return new PageVo(this.page(page, wrapper));
    }

    /**
     * spu相关3张
     * sku相关3张
     * 营销相关3张
     * @param spuInfo
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    @Override
    public void saveBigSpu(SpuInfoVO spuInfo) {

        //1.新增spu相关3张
        //1.1 新增spuInfo
        //默认是已上架
        spuInfo.setPublishStatus(1);
        spuInfo.setCreateTime(new Date());
        spuInfo.setUodateTime(spuInfo.getCreateTime());
        this.save(spuInfo);
        //获取新增之后的spuId
        Long spuId = spuInfo.getId();

        //1.2 新增spuInfoDesc
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        // 注意：spu_info_desc表的主键是spu_id,需要在实体类中配置该主键不是自增主键
        spuInfoDescEntity.setSpuId(spuId);
        //采用描述表存储图片信息，图片表不用了
        spuInfoDescEntity.setDecript(StringUtils.join(spuInfo.getSpuImages(), ","));
        spuInfoDescDao.insert(spuInfoDescEntity);

        //1.3 新增productAttrValue
        List<ProductAttrValueVO> baseAttrs = spuInfo.getBaseAttrs();
        if (!CollectionUtils.isEmpty(baseAttrs)){
            List<ProductAttrValueEntity> productAttrValueEntities =
                    baseAttrs.stream().map(productAttrValueVO -> {
                        productAttrValueVO.setSpuId(spuId);
                        productAttrValueVO.setAttrSort(0);
                        productAttrValueVO.setQuickShow(1);
                        return productAttrValueVO;
                    }).collect(Collectors.toList());

            productAttrValueService.saveBatch(productAttrValueEntities);
        }


        //2.新增sku相关3张 spuId
        List<SkuInfoVO> skus = spuInfo.getSkus();
        if (CollectionUtils.isEmpty(skus)) {
            return;
        }

        skus.forEach(skuInfoVO -> {
            //2.1 新增skuInfo
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            BeanUtils.copyProperties(skuInfoVO, skuInfoEntity);
            //这几个参数没有
            skuInfoEntity.setBrandId(spuInfo.getBrandId());
            skuInfoEntity.setCatalogId(spuInfo.getCatalogId());
            skuInfoEntity.setSpuId(spuId);
            skuInfoEntity.setSkuCode(UUID.randomUUID().toString().substring(10).toUpperCase());

            //获取图片集
            List<String> images = skuInfoVO.getImages();
            if (!CollectionUtils.isEmpty(images)){
                //如果没有默认图片就把图集中的第一张设置为默认图片
                skuInfoEntity.setSkuDefaultImg(skuInfoEntity.getSkuDefaultImg()==null ?
                        images.get(0):skuInfoEntity.getSkuDefaultImg());
            }
            //插入数据
            skuInfoDao.insert(skuInfoEntity);
            //获得skuId属性
            Long skuId = skuInfoEntity.getSkuId();

            //2.2 新增sku图片
            if (!CollectionUtils.isEmpty(images)){
                //获得默认图
                String defaultImg = images.get(0);
                List<SkuImagesEntity> skuImagesEntities = images.stream().map(s -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setDefaultImg(StringUtils.equals(defaultImg, s) ? 1 : 0);

                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(s);
                    skuImagesEntity.setImgSort(0);

                    return skuImagesEntity;
                }).collect(Collectors.toList());

                skuImagesService.saveBatch(skuImagesEntities);
            }
            //2.3 新增销售属性
            List<SkuSaleAttrValueEntity> saleAttrs = skuInfoVO.getSaleAttrs();
            saleAttrs.forEach(saleAttr -> {
                saleAttr.setAttrName(attrDao.selectById(saleAttr.getAttrId()).getAttrName());
                saleAttr.setAttrSort(0);
                saleAttr.setSkuId(skuId);
            });

            this.skuSaleAttrValueService.saveBatch(saleAttrs);



            // 3. 保存营销相关信息，需要远程调用gmall-sms
            SkuSaleDTO skuSaleDTO = new SkuSaleDTO();
            BeanUtils.copyProperties(skuInfoVO, skuSaleDTO);
            skuSaleDTO.setSkuId(skuId);
            this.skuSaleFeign.saveSkuSaleInfo(skuSaleDTO);

        });
       //System.out.println(1 / 0); 测试分布式事务
    }
}