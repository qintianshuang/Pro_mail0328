package com.atguigu.gmall.manage.controller;

import com.atguigu.gmall.bean.SkuInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.dubbo.config.annotation.Reference;

import java.util.List;

@Controller
public class SkuController {

    @Reference
    private com.atguigu.gmall.service.SkuService skuService;

    @RequestMapping("getSkuListBySpu")
    @ResponseBody
    public List<SkuInfo> getSkuListBySpu(String spuId){
        List<SkuInfo> skuInfoList = skuService.getSkuListBySpu(spuId);
        return skuInfoList;
    }

    @RequestMapping("saveSku")
    @ResponseBody
    public String saveSku(SkuInfo skuInfo){
        skuService.saveSku(skuInfo);
        return "sucess";
    }
}
