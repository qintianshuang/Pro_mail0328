package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class CatalogServiceImpl implements com.atguigu.gmall.service.CatalogService {

    @Autowired
    private com.atguigu.gmall.manage.mapper.Catalog1Mapper catalog1Mapper;

    @Autowired
    private com.atguigu.gmall.manage.mapper.Catalog2Mapper catalog2Mapper;

    @Autowired
    private com.atguigu.gmall.manage.mapper.Catalog3Mapper catalog3Mapper;

    @Override
    public List<com.atguigu.gmall.bean.BaseCatalog1> getCatalog1() {
        return catalog1Mapper.selectAll();
    }

    @Override
    public List<com.atguigu.gmall.bean.BaseCatalog2> getCatalog2(String catalog1Id) {
        com.atguigu.gmall.bean.BaseCatalog2 baseCatalog2 = new com.atguigu.gmall.bean.BaseCatalog2();
        baseCatalog2.setCatalog1Id(catalog1Id);
        List<com.atguigu.gmall.bean.BaseCatalog2> select = catalog2Mapper.select(baseCatalog2);
        return select;

    }

    @Override
    public List<com.atguigu.gmall.bean.BaseCatalog3> getCatalog3(String catalog2Id) {
        com.atguigu.gmall.bean.BaseCatalog3 baseCatalog3 = new com.atguigu.gmall.bean.BaseCatalog3();
        baseCatalog3.setCatalog2Id(catalog2Id);
        List<com.atguigu.gmall.bean.BaseCatalog3> select = catalog3Mapper.select(baseCatalog3);
        return select;
    }
}
