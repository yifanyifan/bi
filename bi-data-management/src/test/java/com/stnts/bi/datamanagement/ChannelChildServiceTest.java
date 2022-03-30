package com.stnts.bi.datamanagement;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.stnts.bi.datamanagement.module.channel.entity.Channel;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelChild;
import com.stnts.bi.datamanagement.module.channel.param.ChannelPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelChildService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelService;
import com.stnts.bi.entity.common.PageEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/3/11
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ChannelChildServiceTest {

    @Autowired
    private ChannelChildService channelChildService;

    @Test
    public void getChannelPageListByCcidTest(){

        /*try {
            List<ChannelChild> list = channelChildService.getChannelChildListByCcid("CCID53026CPDgah");
            Assert.assertNotNull(list);
            list.forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    /*@Test
    public void addTest(){

        ChannelChild channelChild = new ChannelChild();
        channelChild.setSubChannelName("zl的测试子渠道");
        try {
            Map<Object, Object> objectObjectMap = channelChildService.saveChannelChild(channelChild);
            System.out.println(JSON.toJSONString(objectObjectMap));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
