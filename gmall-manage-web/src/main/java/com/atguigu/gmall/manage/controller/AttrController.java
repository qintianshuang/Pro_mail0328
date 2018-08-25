package com.atguigu.gmall.manage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.dubbo.config.annotation.Reference;

import java.util.List;

@Controller
public class AttrController {

    @Reference
    private com.atguigu.gmall.service.AttrService attrService;


    @RequestMapping("/getAttrListByCtg3Id")
    @ResponseBody
    public List<com.atguigu.gmall.bean.BaseAttrInfo> getAttrListByCtg3Id(String catalog3Id){
        List<com.atguigu.gmall.bean.BaseAttrInfo> baseAttrInfos = attrService.getAttrListByCtg3Id(catalog3Id);
        return baseAttrInfos;
    }


    @RequestMapping("/getAttrList")
    @ResponseBody
    public List<com.atguigu.gmall.bean.BaseAttrInfo> getAttrList(String catalog3Id){
        List<com.atguigu.gmall.bean.BaseAttrInfo> baseAttrInfoList = attrService.getAttrList(catalog3Id);
        return baseAttrInfoList;
    }

    @RequestMapping("/saveAttr")
    @ResponseBody
    public String saveAttr(com.atguigu.gmall.bean.BaseAttrInfo baseAttrInfo){
        attrService.saveAttr(baseAttrInfo);
        return "success";
    }

    @RequestMapping("/getAttrValueList")
    @ResponseBody
    public List<com.atguigu.gmall.bean.BaseAttrValue> getAttrValue(String attrInfoId){
        List<com.atguigu.gmall.bean.BaseAttrValue> attrValueList = attrService.getAttrValue(attrInfoId);
        return attrValueList;
    }

    @RequestMapping("/deleteAttrInfo")
    @ResponseBody
    public String deleteAttrInfo(String attrDelInfoId){
        attrService.deleteAttrInfo(attrDelInfoId);
        return "success";
    }
}
