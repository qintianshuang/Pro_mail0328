package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.SkuAttrValue;
import com.atguigu.gmall.bean.SkuImage;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SkuSaleAttrValue;
import com.atguigu.gmall.manage.mapper.SkuAttrValueMapper;
import com.atguigu.gmall.manage.mapper.SkuImageMapper;
import com.atguigu.gmall.manage.mapper.SkuInfoMapper;
import com.atguigu.gmall.manage.mapper.SkuSaleAttrValueMapper;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    private SkuImageMapper skuImageMapper;

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<SkuInfo> getSkuListBySpu(String spuId) {
       SkuInfo skuInfo = new SkuInfo();
        skuInfo.setSpuId(spuId);
        List<com.atguigu.gmall.bean.SkuInfo> skuInfoList = skuInfoMapper.select(skuInfo);
        return skuInfoList;
    }

    @Override
    public void saveSku(SkuInfo skuInfo) {
        skuInfoMapper.insertSelective(skuInfo);
        String skuId = skuInfo.getId();

        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue skuAttrValue : skuAttrValueList) {
            skuAttrValue.setSkuId(skuId);
            skuAttrValueMapper.insert(skuAttrValue);
        }

        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            skuSaleAttrValue.setSkuId(skuId);
            skuSaleAttrValueMapper.insert(skuSaleAttrValue);
        }


        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage skuImage : skuImageList) {
            skuImage.setSkuId(skuId);
            skuImageMapper.insert(skuImage);
        }
    }

    @Override
    public SkuInfo getSkuById(String skuId) {
        Jedis jedis = redisUtil.getJedis();
        SkuInfo skuInfo = null;
        System.out.println();
        //查看redis缓存
        String key = "sku:" + skuId + ":info";
        String val = jedis.get(key);

        if("empty".equals(val)){
            System.out.println(Thread.currentThread() + "发现数据库中暂时没有商品，直接返回空对象");
            return skuInfo;
        }

        skuInfo = JSON.parseObject(val, SkuInfo.class);
        if (StringUtils.isBlank(val)){
            System.out.println(Thread.currentThread() + "发现缓存中没有数据，申请分步式锁");
            //申请缓存锁
            String OK = jedis.set("sku:" + skuId + ":lock", "1", "nx", "px", 3000);

            //判断是否为空
            if ("OK".equals(OK)){
                //没有则去数据库查询
                System.out.println(Thread.currentThread() + "获取到分布式锁，开始访问数据库");
                skuInfo = getSkuByIdFormDb(skuId);

                //同步缓存同步锁
                if (skuInfo != null){
                    System.out.println(Thread.currentThread() + "通过分布式锁，查询到数据，开始同步缓存");
                    //同步缓存
                    jedis.set(key,JSON.toJSONString(skuInfo));
                } else {
                    System.out.println(Thread.currentThread() + "通过分布式锁，没有查询到数据，通知同伴5秒内不要访问");
                    //通知同伴
                    jedis.setex("sku:" + skuId + ":lock", 5,"empty");
                }
                System.out.println(Thread.currentThread() + "查询到数据，归还分布式锁");
                //归还锁
                jedis.del("sku:" + skuId + ":lock");
            } else {//没有拿到缓存锁
                //自旋
                try {
                    System.out.println(Thread.currentThread() + "发现分布式锁被占用，开始自旋");
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread() + "自旋结束，重新查询");
                getSkuById(skuId);
            }
        }else {
            System.out.println(Thread.currentThread() + "正常从缓存中取出数据");
            skuInfo = JSON.parseObject(val, SkuInfo.class);
        }
        System.out.println(Thread.currentThread() + "返回结果");
        return skuInfo;
    }

    @Override
    public List<SkuInfo> getSkuListByCatalog3Id(String catalog3Id) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setCatalog3Id(catalog3Id);
        List<SkuInfo> select = skuInfoMapper.select(skuInfo);

        for (SkuInfo info : select) {
            String infoId = info.getId();
            SkuAttrValue skuAttrValue = new SkuAttrValue();
            skuAttrValue.setSkuId(infoId);
            List<SkuAttrValue> select1 = skuAttrValueMapper.select(skuAttrValue);

            info.setSkuAttrValueList(select1);

        }

        return select;
    }


    private SkuInfo getSkuByIdFormDb(String skuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        SkuInfo skuInfo1 = skuInfoMapper.selectOne(skuInfo);

        //图片
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        List<SkuImage> select = skuImageMapper.select(skuImage);
        skuInfo1.setSkuImageList(select);
        return skuInfo1;
    }

    @Override
    public boolean checkPrice(BigDecimal skuPrice, String skuId) {
        boolean b = false;

        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(skuId);
        SkuInfo sku = skuInfoMapper.selectOne(skuInfo);

        if (sku != null){
            int i = sku.getPrice().compareTo(skuPrice);
            if (i == 0){
                b = true;
            }
        }

        return b;
    }
}
