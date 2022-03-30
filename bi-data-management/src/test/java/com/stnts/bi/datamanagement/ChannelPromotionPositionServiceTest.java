package com.stnts.bi.datamanagement;

import com.stnts.bi.datamanagement.module.channel.entity.ChannelPromotionPosition;
import com.stnts.bi.datamanagement.module.channel.param.ChannelPromotionPositionPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelPromotionPositionService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/4/2
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ChannelPromotionPositionServiceTest {

    @Autowired
    private ChannelPromotionPositionService channelPromotionPositionService;

    @Test
    public void getListTest(){

        ChannelPromotionPositionPageParam channelPromotionPositionPageParam = new ChannelPromotionPositionPageParam();
//        channelPromotionPositionPageParam.setPid("");
        channelPromotionPositionPageParam.setChannelId(1377808089201377281L);
        channelPromotionPositionPageParam.setPid("YYDT12349YiZ1");

        try {
            List<ChannelPromotionPosition> positionList = channelPromotionPositionService.getChannelPromotionPositionList(channelPromotionPositionPageParam);
            positionList.forEach(System.out::println);
            Assert.assertNotNull(positionList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
