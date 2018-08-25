package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.BaseSaleAttr;
import com.atguigu.gmall.bean.SpuImage;
import com.atguigu.gmall.bean.SpuInfo;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.gmall.manage.util.MyUploadUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class SpuController {

    @Reference
    private com.atguigu.gmall.service.SpuService spuService;

    //fdfs上传
    @RequestMapping("fileUpload")
    @ResponseBody
    public String fileUpload(@RequestParam("file") MultipartFile file){
        //fdfs上传工具
        String imageUrl = MyUploadUtil.uploadImage(file);
        //http://192.168.141.150/group1/M00/00/00/wKiNlltnSquAf2vRAAUwMjvQ4VQ567.jpg
        return imageUrl;
    }

    //
    @RequestMapping("spuList")
    @ResponseBody
    public List<SpuInfo> spuList(String catalog3Id){
        List<SpuInfo> spuInfoList = spuService.spuList(catalog3Id);
        return spuInfoList;
    }

    @RequestMapping("saveSpu")
    @ResponseBody
    public String saveSpu(com.atguigu.gmall.bean.SpuInfo spuInfo){
        spuService.saveSpu(spuInfo);
        return "sucess";
    }


    @RequestMapping("baseSaleAttrList")
    @ResponseBody
    public List<BaseSaleAttr> baseSaleAttrList(){
        List<BaseSaleAttr> baseSaleAttrList = spuService.baseSaleAttrList();
        return baseSaleAttrList;
    }

    //sku获取初始化销售属性数据库信息
    @RequestMapping("getSaleAttrListBySpuId")
    @ResponseBody
    public List<SpuSaleAttr> getSaleAttrListBySpuId(String spuId){
        List<SpuSaleAttr> spuSaleAttrLists = spuService.getSaleAttrListBySpuId(spuId);
        return spuSaleAttrLists;
    }

    //sku获取初始化图片信息数据库信息
    @RequestMapping("getSpuImageListBySpuId")
    @ResponseBody
    public List<SpuImage> getSpuImageListBySpuId(String spuId){
        List<SpuImage> spuImages = spuService.getSpuImageListBySpuId(spuId);
        return spuImages;
    }
}
