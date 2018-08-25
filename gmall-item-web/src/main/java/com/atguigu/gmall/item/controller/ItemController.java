package com.atguigu.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SkuSaleAttrValue;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {

    @Reference
    private SkuService skuService;

    @Reference
    private SpuService spuService;

    @RequestMapping("index")
    public String index(ModelMap map){
        //map.put("hello","hello thymeleaf");
        List<UserInfo> userInfos = new ArrayList<UserInfo>();
        for (int i = 0; i < 5; i++) {
            UserInfo userInfo = new UserInfo();
            userInfo.setNickName("小"+i);
            userInfo.setPhoneNum("12312312312");
            userInfos.add(userInfo);
        }
        map.put("userInfos",userInfos);
        return "demo";
    }

    @RequestMapping("{skuId}.html")
    public String item(@PathVariable String skuId, ModelMap map){

        SkuInfo skuInfo = skuService.getSkuById(skuId);
        map.put("skuInfo",skuInfo);
        String spuId = skuInfo.getSpuId();

        //当前sku所包含销售属性值
        //比如：版本 ，尺寸， 容量 。。。。。
        //List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        //
        ////spu销售属性列表
        //List<SpuSaleAttr> saleAttrListBySpuId = spuService.getSaleAttrListBySpuId(spuId);
        //map.put("spuSaleAttrListCheckBySku",saleAttrListBySpuId);
        Map<String,String> stringStringHashMap = new HashMap<String, String>();
        List<SkuInfo> skuInfos = spuService.getSkuSaleAttrListCheckBySpu(spuId);

        for (SkuInfo info : skuInfos) {
            String v = info.getId();

            String k = "";
            List<SkuSaleAttrValue> skuSaleAttrValueList = info.getSkuSaleAttrValueList();
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                k = k + "|" + skuSaleAttrValue.getSaleAttrValueId();
            }
            stringStringHashMap.put(k,v);
        }
        String skuJson = JSON.toJSONString(stringStringHashMap);
        map.put("skuJson",skuJson);

        //当前sku所包含销售属性值... 比如：版本 ，尺寸， 容量 。。。。。
        //销售属性列表
        Map<String,String> stringStringMap = new HashMap<String, String>();
        stringStringMap.put("spuId",spuId);
        stringStringMap.put("skuId",skuId);
        List<SpuSaleAttr> saleAttrListBySpuId = spuService.getSpuSaleAttrListCheckBySku(stringStringMap);

        map.put("spuSaleAttrListCheckBySku",saleAttrListBySpuId);
        return "item";
    }
}
