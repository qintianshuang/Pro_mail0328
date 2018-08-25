package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.BaseCatalog2;
import com.atguigu.gmall.bean.BaseCatalog3;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class CataLogController {

 @Reference
 private com.atguigu.gmall.service.CatalogService catalogService;

 @RequestMapping("getCatalog1")
 @ResponseBody
 public List<com.atguigu.gmall.bean.BaseCatalog1> getCatalog1(){
  List<com.atguigu.gmall.bean.BaseCatalog1> baseCatalog1List = catalogService.getCatalog1();
  return baseCatalog1List;
 }

 @RequestMapping("getCatalog2")
 @ResponseBody
 public List<BaseCatalog2> getCatalog2(String catalog1Id){
  List<BaseCatalog2> baseCatalog2List = catalogService.getCatalog2(catalog1Id);
  return baseCatalog2List;
 }

 @RequestMapping("getCatalog3")
 @ResponseBody
 public List<BaseCatalog3> getCatalog3(String catalog2Id){
  List<BaseCatalog3> baseCatalog3List = catalogService.getCatalog3(catalog2Id);
  return baseCatalog3List;
 }
}
