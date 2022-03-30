package com.stnts.bi.gameop.mapper;

import com.stnts.bi.entity.gameop.DimPidUser;
import com.stnts.bi.mapper.gameop.DimPidUserMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/12/15
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class DimPidUserMapperTest {

    @Autowired
    private DimPidUserMapper dimPidUserMapper;

    @Test
    public void testInsert(){

        DimPidUser dimPidUser = new DimPidUser();
        dimPidUser.setId(4);
        dimPidUser.setUserId(2239);
        dimPidUser.setCnname("张良");
        dimPidUser.setGameNames("英雄联盟");
        dimPidUser.setChannelNames("QQ");
        dimPidUser.setPids("ABCDEFG");

        int i = dimPidUserMapper.insertNew(dimPidUser);
        Assert.assertTrue(i > 0);
    }
}
