package com.stnts.bi.datamanagement;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.stnts.bi.datamanagement.module.channel.entity.Channel;
import com.stnts.bi.datamanagement.module.channel.param.ChannelPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelService;
import com.stnts.bi.entity.common.PageEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.IntStream;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/3/11
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ChannelServiceTest {

    @Autowired
    private ChannelService channelService;

    @Test
    public void getChannelPageListTest(){

        /*ChannelPageParam param = new ChannelPageParam();

        param.setDepartmentCode("01.07");
        param.setChannelId(1372126519176540162L);
//        param.setCompanyId(5618L);
//        param.setKeyword("CCID2016");

        try {
            PageEntity<Channel> channelPageList = channelService.getChannelPageList(param);
            Assert.assertNotNull(channelPageList.getRecords());
            channelPageList.getRecords().forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Test
    public void getChannelListTest(){

        ChannelPageParam param = new ChannelPageParam();

//        param.setDepartmentCode("01.07");
//        param.setCompanyId(11883L);
//        param.setCompanyId(5618L);
//        param.setKeyword("CCID2016");

//        try {
//            List<Channel> channelPageList = channelService.getChannelList(param);
//            Assert.assertNotNull(channelPageList);
//            channelPageList.forEach(System.out::println);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Test
    public void initChannelId(){
        IntStream.range(0, 30).forEach(i -> System.out.println(IdWorker.getId()));
    }
}
