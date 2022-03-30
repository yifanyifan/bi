package com.stnts.bi.datamanagement;

import com.stnts.bi.datamanagement.module.channel.entity.ChannelProduct;
import com.stnts.bi.datamanagement.module.channel.param.ChannelProductPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelProductService;
import com.stnts.bi.entity.common.PageEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/2/25
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ChannelProductServiceTest {

    @Autowired
    private ChannelProductService channelProductService;

    @Test
    public void getChannelProductPageListExtTest(){

        /*ChannelProductPageParam channelProductPageParam = new ChannelProductPageParam();
        channelProductPageParam.setKeyword("盛天");
        channelProductPageParam.setCooperationMainName("老板老板");

        try {
            PageEntity<ChannelProduct> pageEntity = channelProductService.getChannelProductPageListExt(channelProductPageParam);
            pageEntity.getRecords().forEach(System.out::println);
            Assert.assertNotNull(pageEntity.getRecords());
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Test
    public void update() throws Exception {

        ChannelProduct channelProduct = new ChannelProduct();
        channelProduct.setDepartmentCode("01.09");
        channelProduct.setDepartmentName("研发中心");
        channelProduct.setCooperationMainName("11111");
        //channelProduct.setProductId(766L);
        channelProduct.setProductCode("zy_test01");
        channelProduct.setUserid(10498L);
        channelProduct.setUsername("朱云");

        channelProduct.setProductName("我测试一下");

        channelProductService.updateChannelProduct(channelProduct);
    }
}
