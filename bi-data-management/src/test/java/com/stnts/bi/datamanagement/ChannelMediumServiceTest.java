package com.stnts.bi.datamanagement;

import com.stnts.bi.datamanagement.module.channel.entity.ChannelMedium;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelProduct;
import com.stnts.bi.datamanagement.module.channel.param.ChannelMediumPageParam;
import com.stnts.bi.datamanagement.module.channel.param.ChannelProductPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelMediumService;
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
public class ChannelMediumServiceTest {

    @Autowired
    private ChannelMediumService channelMediumService;

    @Test
    public void getChannelProductPageListExtTest(){

        /*ChannelMediumPageParam param = new ChannelMediumPageParam();
        param.setDepartmentCode("01.14");
//        param.setKeyword("盛天");

        try {
            PageEntity<ChannelMedium> pageEntity = channelMediumService.getChannelMediumPageList(param);
            pageEntity.getRecords().forEach(System.out::println);
            Assert.assertNotNull(pageEntity.getRecords());
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}
