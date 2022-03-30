package com.stnts.bi.datamanagement;

import com.alibaba.fastjson.JSON;
import com.stnts.bi.datamanagement.service.ApiService;
import com.stnts.bi.datamanagement.vo.ApiSimpleVO;
import com.stnts.bi.datamanagement.vo.ApiVO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/5/13
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ApiServiceTest {

    @Autowired
    private ApiService apiService;

    /*@Test
    public void initPid4YlwTest(){

        try {

            ApiVO apiVO = new ApiVO();
            apiVO.setSubChannelName("易乐玩子渠道");
            apiVO.setCompanyName("趣游科技集团有限公司");
            apiVO.setChannelName("易乐玩渠道");
            apiVO.setDepartmentCode("01.07");
            apiVO.setDepartmentName("游戏运营部");
            apiVO.setUserid(2239L);
            apiVO.setUsername("张良");
            apiVO.setFirstLevelBusiness("游戏联运");
            apiVO.setSecondLevelBusiness("CPS联运");
            apiVO.setThirdLevelBusiness("渠道");
            apiVO.setChannelRate(new BigDecimal(1));
            apiVO.setChargeRule("CPS");
            apiVO.setPidAlias("PID别名");
            Map<Object, Object> map = apiService.initPid4Ylw(apiVO);
            System.out.println(JSON.toJSONString(map));
            Assert.assertNotNull(map);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void initPid4YlwSimpleTest(){

        try{

            ApiSimpleVO apiSimpleVO = new ApiSimpleVO();
            apiSimpleVO.setCcid("CCID62689CPSci3");
            apiSimpleVO.setSubChannelName("易乐玩子渠道");
            apiSimpleVO.setPidAlias("简单接口测试别名");
            Map<Object, Object> map = apiService.initPid4YlwSimple(apiSimpleVO);
            System.out.println(JSON.toJSONString(map));
            Assert.assertNotNull(map);
        }catch(Exception e){
            e.printStackTrace();
        }
    }*/
}
