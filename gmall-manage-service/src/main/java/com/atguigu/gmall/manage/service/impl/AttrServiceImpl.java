package com.atguigu.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.BaseAttrInfo;
import com.atguigu.gmall.bean.BaseAttrValue;
import com.atguigu.gmall.manage.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.manage.mapper.BaseAttrValueMapper;
import com.atguigu.gmall.manage.mapper.SelectAttrListByValueIds;
import com.atguigu.gmall.service.AttrService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;


@Service
public class AttrServiceImpl implements AttrService {

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;

    @Autowired
    private SelectAttrListByValueIds selectAttrListByValueIds;

    @Override
    public List<BaseAttrInfo> getAttrList(String catalog3Id) {
        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
        baseAttrInfo.setCatalog3Id(catalog3Id);
        List<BaseAttrInfo> select = baseAttrInfoMapper.select(baseAttrInfo);
        return select;
    }

    @Override
    public void saveAttr(BaseAttrInfo baseAttrInfo) {
        //根据baseAttrInfo判断baseAttrInfo.getId()是否为空，且是否有值
        if (baseAttrInfo.getId() != null && baseAttrInfo.getId().length() > 0){
            //当baseAttrInfo.getId()不为，前台页面没有传递对应的id值，执行修改操作
            baseAttrInfoMapper.updateByPrimaryKey(baseAttrInfo);
        }else {
            //当baseAttrInfo.getId()为空
            //前台页面没有传递baseAttrInfo.getId()的值
            //以上情况将先将baseAttrInfo.getId()置为null
                baseAttrInfo.setId(null);
            //通用mapper中的insertSelective方法插入会返回一个主键
            baseAttrInfoMapper.insertSelective(baseAttrInfo);
        }

        BaseAttrValue baseAttrValue2del = new BaseAttrValue();
        //baseAttrInfo.getId()在数据库中是base_attr_value主键，同时是baseAttrValue的外键
        baseAttrValue2del.setAttrId(baseAttrInfo.getId());
        //根据以上情况根据baseAttrInfo.getId()对baseAttrValue执行删除
        baseAttrValueMapper.delete(baseAttrValue2del);

        if (baseAttrInfo.getAttrValueList() != null && baseAttrInfo.getAttrValueList().size() > 0) {
            List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
            for (BaseAttrValue baseAttrValue : attrValueList) {
                    baseAttrValue.setId(null);
                    baseAttrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insertSelective(baseAttrValue);
            }
        }
   }

    @Override
    public List<BaseAttrValue> getAttrValue(String attrInfoId) {
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(attrInfoId);
        List<BaseAttrValue> baseAttrValueList = baseAttrValueMapper.select(baseAttrValue);
        return baseAttrValueList;
    }

    @Override
    public void deleteAttrInfo(String attrDelInfoId) {
        baseAttrInfoMapper.deleteByPrimaryKey(attrDelInfoId);
        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
        baseAttrInfo.setId(attrDelInfoId);
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        if (attrValueList != null && attrValueList.size() > 0) {
            BaseAttrValue baseAttrValue = new BaseAttrValue();
            baseAttrValue.setAttrId(attrDelInfoId);
            baseAttrValueMapper.delete(baseAttrValue);
        }
    }

    @Override
    public List<BaseAttrInfo> getAttrListByCtg3Id(String catalog3Id) {
        BaseAttrInfo baseAttrInfo = new BaseAttrInfo();
        baseAttrInfo.setCatalog3Id(catalog3Id);
        List<BaseAttrInfo> select = baseAttrInfoMapper.select(baseAttrInfo);

        for (BaseAttrInfo attrInfo : select) {
            String attrInfoId = attrInfo.getId();
            BaseAttrValue baseAttrValue = new BaseAttrValue();
            baseAttrValue.setAttrId(attrInfoId);
            List<BaseAttrValue> select1 = baseAttrValueMapper.select(baseAttrValue);

            attrInfo.setAttrValueList(select1);
        }
        return select;
    }

    @Override
    public List<BaseAttrInfo> getAttrListByValueIds(Set<String> valueIds) {
        String join = StringUtils.join(valueIds,",");
        List<BaseAttrInfo> baseAttrInfoList = baseAttrValueMapper.selectAttrListByValueIds(join);
        return baseAttrInfoList;
    }
}
