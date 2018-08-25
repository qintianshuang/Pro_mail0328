package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.manage.mapper.*;
import com.atguigu.gmall.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuInfoMapper spuInfoMapper;

    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Autowired
    private SpuImageMapper spuImageMapper;

    @Override
    public List<SpuInfo> spuList(String catalog3Id) {
       SpuInfo spuInfo = new com.atguigu.gmall.bean.SpuInfo();
        spuInfo.setCatalog3Id(catalog3Id);
        List<SpuInfo> spuInfoList = spuInfoMapper.select(spuInfo);
        return spuInfoList;
    }

    @Override
    public List<BaseSaleAttr> baseSaleAttrList() {
        List<BaseSaleAttr> baseSaleAttrList = baseSaleAttrMapper.selectAll();
        return baseSaleAttrList;
    }

    @Override
    public void saveSpu(com.atguigu.gmall.bean.SpuInfo spuInfo) {
        //保存SpuInfo，并返回主键
        spuInfoMapper.insertSelective(spuInfo);
        String spuInfoId = spuInfo.getId();
        //保存图片信息

        //保存销售属性
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
            spuSaleAttr.setSpuId(spuInfoId);
            System.out.println(spuSaleAttr);
            spuSaleAttrMapper.insert(spuSaleAttr);

            //保存销售属性值
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
            for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                spuSaleAttrValue.setSpuId(spuInfoId);
                spuSaleAttrValueMapper.insert(spuSaleAttrValue);
            }
        }

        //保存图片信息
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        for (SpuImage spuImage : spuImageList) {
            spuImage.setSpuId(spuInfoId);
            spuImageMapper.insert(spuImage);
        }
    }

    @Override
    public List<SpuSaleAttr> getSaleAttrListBySpuId(String spuId) {
        SpuSaleAttr spuSaleAttr = new SpuSaleAttr();
        spuSaleAttr.setSpuId(spuId);
        List<SpuSaleAttr> spuSaleAttrLists = spuSaleAttrMapper.select(spuSaleAttr);

        for (SpuSaleAttr saleAttr : spuSaleAttrLists) {
            String saleAttrId = saleAttr.getSaleAttrId();

            SpuSaleAttrValue spuSaleAttrValue = new SpuSaleAttrValue();
            spuSaleAttrValue.setSaleAttrId(saleAttrId);
            spuSaleAttrValue.setSpuId(spuId);
            List<SpuSaleAttrValue> select = spuSaleAttrValueMapper.select(spuSaleAttrValue);
            saleAttr.setSpuSaleAttrValueList(select);
        }
        return spuSaleAttrLists;
    }

    @Override
    public List<SpuImage> getSpuImageListBySpuId(String spuId) {
        SpuImage spuImage = new SpuImage();
        spuImage.setSpuId(spuId);
        List<SpuImage> images = spuImageMapper.select(spuImage);
        return images;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Map<String, String> stringStringMap) {

        List<SpuSaleAttr> spuSaleAttrList = spuSaleAttrValueMapper.selectSpuSaleAttrListCheckBySku(stringStringMap);

        return spuSaleAttrList;
    }

    @Override
    public List<SkuInfo> getSkuSaleAttrListCheckBySpu(String spuId) {
        List<SkuInfo> skuInfos = spuSaleAttrValueMapper.selectSkuSaleAttrValueListBySpu(spuId);
        return skuInfos;
    }
}
