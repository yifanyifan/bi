package com.stnts.bi.datamanagement;

import com.alibaba.fastjson.JSON;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.module.channel.param.ChannelPromotionPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelPromotionService;
import com.stnts.bi.datamanagement.module.channel.vo.AppVO;
import com.stnts.bi.datamanagement.module.channel.vo.ChannelPromotionVO;
import com.stnts.bi.datamanagement.module.channel.vo.DepartmentVO;
import com.stnts.bi.vo.DmVO;
import com.stnts.bi.entity.common.PageEntity;
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
 * @date: 2021/2/26
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ChannelPromotionServiceTest {

    @Autowired
    private ChannelPromotionService channelPromotionService;

    @Test
    public void listChannelCooperationTest(){

       /* ChannelPromotionPageParam param = new ChannelPromotionPageParam();
//        param.setDepartmentCode("01.07");
//        param.setSubChannelId(1367441631282405378L);
//        param.setChannelId(1367376969966456834L);
//        param.setAgentId(5563L);
//        param.setPidList(Arrays.asList("YYDT12346rV0"));
//        param.setKeyword("易");

        try {
            PageEntity<ChannelPromotionVO> pageEntity = channelPromotionService.getPidPageList(param);
            pageEntity.getRecords().stream().forEach(System.out::println);
            Assert.assertNotNull(pageEntity.getRecords());
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Test
    public void getProductAndAppByCcidTest(){

        /*ChannelPromotionPageParam param = new ChannelPromotionPageParam();
        param.setCcid("CCID12578CPSh43");
        try {

            PageEntity<AppVO> productAndAppByCcid = channelPromotionService.getProductAndAppByCcid(param);
            productAndAppByCcid.getRecords().forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    @Test
    public void listDepartmentTest(){

        ChannelPromotionPageParam param = new ChannelPromotionPageParam();
        param.setAgentId(11882L);
        try {

            ResultEntity<List<DepartmentVO>> resultEntity = channelPromotionService.listDepartment(param);
            resultEntity.getData().forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
