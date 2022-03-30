package com.stnts.bi.datamanagement;

import cn.hutool.core.lang.Assert;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelCooperation;
import com.stnts.bi.datamanagement.module.channel.param.ChannelCooperationPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelCooperationService;
import com.stnts.bi.entity.common.PageEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/2/26
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ChannelCooperationServiceTest {

    @Autowired
    private ChannelCooperationService channelCooperationService;

    @Test
    public void listChannelCooperationTest(){

        /*ChannelCooperationPageParam params = new ChannelCooperationPageParam();
        try {
            PageEntity<ChannelCooperation> pageEntity = channelCooperationService.getChannelCooperationPageListExt(params);
            pageEntity.getRecords().stream().forEach(System.out::println);
            Assert.notNull(pageEntity.getRecords());
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}
