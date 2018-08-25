package com.atguigu.gmall.list;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SkuLsInfo;
import com.atguigu.gmall.service.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallListServiceApplicationTests {

    @Autowired
    private JestClient jestClient;

    @Reference
    private SkuService skuService;

    //@Test
    //public void searchTest(){
    //    List<SkuLsInfo> skuLsInfos = new ArrayList<>();
    //
    //    Search search = new Search.Builder("GET gmall0328/SkuLsInfo/_search\\n\" +\n" +
    //            "                \"{\\n\" +\n" +
    //            "                \"  \\\"query\\\": {\\n\" +\n" +
    //            "                \"    \\\"bool\\\": {\\n\" +\n" +
    //            "                \"      \\\"filter\\\": [\\n\" +\n" +
    //            "                \"        {\\n\" +\n" +
    //            "                \"        \\\"term\\\":{\\n\" +\n" +
    //            "                \"          \\\"catalog3Id\\\": \\\"61\\\"\\n\" +\n" +
    //            "                \"        }},\\n\" +\n" +
    //            "                \"       {\\n\" +\n" +
    //            "                \"        \\\"term\\\":{\\n\" +\n" +
    //            "                \"          \\\"skuAttrValueList.valueId\\\": \\\"51\\\"\\n\" +\n" +
    //            "                \"        }},\\n\" +\n" +
    //            "                \"        {\\n\" +\n" +
    //            "                \"        \\\"term\\\":{\\n\" +\n" +
    //            "                \"          \\\"skuAttrValueList.valueId\\\": \\\"48\\\"\\n\" +\n" +
    //            "                \"        }}\\n\" +\n" +
    //            "                \"      ],\\n\" +\n" +
    //            "                \"      \\\"must\\\": [\\n\" +\n" +
    //            "                \"        {\\n\" +\n" +
    //            "                \"          \\\"match\\\": {\\n\" +\n" +
    //            "                \"            \\\"skuName\\\": \\\"小米\\\"\\n\" +\n" +
    //            "                \"          }\\n\" +\n" +
    //            "                \"        }\\n\" +\n" +
    //            "                \"      ]\\n\" +\n" +
    //            "                \"    }\\n\" +\n" +
    //            "                \"  }\\n\" +\n" +
    //            "                \"}\\n").addIndex("gmall0328").addType("SkuLsInfo").build();
    //
    //    try {
    //        SearchResult execute = jestClient.execute(search);
    //
    //        List<SearchResult.Hit<SkuLsInfo, Void>> hits = execute.getHits(SkuLsInfo.class);
    //        for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
    //
    //            SkuLsInfo source = hit.source;
    //            skuLsInfos.add(source);
    //        }
    //
    //        System.out.println(skuLsInfos.size());
    //    } catch (IOException e) {
    //        e.printStackTrace();
    //    }
    //}


    @Test
    public void contextLoads() {

        // 查询mysql中的sku信息
        List<SkuInfo> skuInfoList = skuService.getSkuListByCatalog3Id("61");

        if(skuInfoList != null && skuInfoList.size() > 0){
            // 转化es中的sku信息
            List<SkuLsInfo> skuLsInfos = new ArrayList<>();

            for (SkuInfo skuInfo : skuInfoList) {
                SkuLsInfo skuLsInfo = new SkuLsInfo();

                try {
                    BeanUtils.copyProperties(skuLsInfo,skuInfo);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                skuLsInfos.add(skuLsInfo);
            }

            // 导入到es中
            for (SkuLsInfo skuLsInfo : skuLsInfos) {
                String id = skuLsInfo.getId();
                //long skuId = Long.parseLong(id);
                Index build = new Index.Builder(skuLsInfo).index("gmall0328").type("SkuLsInfo").id(id).build();

                System.out.println(build.toString());
                try {
                    jestClient.execute(build);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
