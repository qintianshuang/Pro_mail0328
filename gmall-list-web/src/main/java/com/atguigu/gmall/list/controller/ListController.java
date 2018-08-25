package com.atguigu.gmall.list.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.service.AttrService;
import com.atguigu.gmall.service.ListService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
public class ListController {

    @Reference
    private ListService listService;

    @Reference
    private AttrService attrService;

    @RequestMapping("list.html")
    public String search(SkuLsParam skuLsParam, ModelMap map) {
        List<SkuLsInfo> skuLsInfos = listService.search(skuLsParam);

        //封装平台属性
        //去重，排除已选中的属性
        List<BaseAttrInfo> baseAttrInfos = getAttrValueIds(skuLsInfos);
        //已经存在的id或者是选中的id
        String[] valueId = skuLsParam.getValueId();
        List<Crumb> crumbs = new ArrayList<>();//面包屑
        if (valueId != null && valueId.length > 0) {

            for (String s : valueId) {
                Iterator<BaseAttrInfo> iterator = baseAttrInfos.iterator();
                //遍历catalog3id集合中所有的平台属性
                while (iterator.hasNext()) {
                    BaseAttrInfo baseAttrInfo = iterator.next();
                    List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
                    for (BaseAttrValue baseAttrValue : attrValueList) {
                        //catalog3id集合中所有的平台属性id
                        if (baseAttrValue.getId().equals(s)) {
                            Crumb crumb = new Crumb();
                            //制作面包屑
                            String urlParamForCrumb = getUrlParamForCrumb(skuLsParam, s);
                            //制作面包屑name
                            String valueName = "";
                            valueName = baseAttrValue.getValueName();
                            //封装好面包屑
                            crumb.setUrlParam(urlParamForCrumb);
                            crumb.setValueName(valueName);
                            crumbs.add(crumb);
                            //将选中的id值删除
                            iterator.remove();
                        }
                    }
                }
            }
        }

        String urlParam = getUrlParam(skuLsParam);
        map.put("urlParam", urlParam);
        map.put("attrValueSelectedList", crumbs);
        map.put("attrList", baseAttrInfos);
        map.put("skuLsInfoList", skuLsInfos);
        return "list";
    }

    /***
     * 制做面包屑url
     * @param skuLsParam
     * @return
     */
    private String getUrlParamForCrumb(SkuLsParam skuLsParam, String id) {
        String urlParam = "";

        String[] valueId = skuLsParam.getValueId();
        String keyword = skuLsParam.getKeyword();
        String catalog3Id = skuLsParam.getCatalog3Id();

        if (StringUtils.isNotBlank(keyword)) {
            if (StringUtils.isBlank(urlParam)) {
                urlParam = "keyword=" + keyword;
            } else {
                urlParam = urlParam + "&keyword=" + keyword;
            }
        }

        if (StringUtils.isNotBlank(catalog3Id)) {
            if (StringUtils.isBlank(urlParam)) {
                urlParam = "catalog3Id=" + catalog3Id;
            } else {
                urlParam = urlParam + "&catalog3Id=" + catalog3Id;
            }
        }

        if (valueId != null && valueId.length > 0) {
            for (String s : valueId) {
                if (!id.equals(s))
                    urlParam = urlParam + "&valueId=" + s;
            }
        }
        return urlParam;
    }

    /***
     * 制做普通url
     * @param skuLsParam
     * @return
     */
    private String getUrlParam(SkuLsParam skuLsParam) {
        String urlParam = "";

        String[] valueId = skuLsParam.getValueId();
        String keyword = skuLsParam.getKeyword();
        String catalog3Id = skuLsParam.getCatalog3Id();

        if (StringUtils.isNotBlank(keyword)) {
            if (StringUtils.isBlank(urlParam)) {
                urlParam = "keyword=" + keyword;
            } else {
                urlParam = urlParam + "&keyword=" + keyword;
            }
        }

        if (StringUtils.isNotBlank(catalog3Id)) {
            if (StringUtils.isBlank(urlParam)) {
                urlParam = "catalog3Id=" + catalog3Id;
            } else {
                urlParam = urlParam + "&catalog3Id=" + catalog3Id;
            }
        }

        if (valueId != null && valueId.length > 0) {
            for (String s : valueId) {
                urlParam = urlParam + "&valueId=" + s;
            }
        }
        return urlParam;
    }

    /***
     * sku的平台属性列表
     * @param skuLsInfo
     * @return
     */
    private List<BaseAttrInfo> getAttrValueIds(List<SkuLsInfo> skuLsInfo) {

        Set<String> valueIds = new HashSet<>();

        for (SkuLsInfo lsInfo : skuLsInfo) {
            List<SkuLsAttrValue> skuAttrValueList = lsInfo.getSkuAttrValueList();

            for (SkuLsAttrValue skuLsAttrValue : skuAttrValueList) {
                String valueId = skuLsAttrValue.getValueId();
                valueIds.add(valueId);
            }
        }
        // 根据去重后的id集合检索，关联到的平台属性列表
        List<BaseAttrInfo> baseAttrInfos = new ArrayList<>();
        baseAttrInfos = attrService.getAttrListByValueIds(valueIds);

        return baseAttrInfos;
    }

    @RequestMapping("index")
    public String index() {
        return "index";
    }
}


