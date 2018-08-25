package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.BaseAttrInfo;
import com.atguigu.gmall.bean.BaseAttrValue;

import java.util.List;
import java.util.Set;

public interface AttrService {

    List<BaseAttrInfo> getAttrList(String catalog3Id);

    void saveAttr(BaseAttrInfo baseAttrInfo);

    List<BaseAttrValue> getAttrValue(String attrInfoId);

    void deleteAttrInfo(String attrDelInfoId);

    List<BaseAttrInfo> getAttrListByCtg3Id(String catalog3Id);

    List<BaseAttrInfo> getAttrListByValueIds(Set<String> valueIds);
}
