package com.stnts.bi.sys.service;

import com.stnts.bi.vo.OlapPermSubVO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/1/13
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestOlapMenuService {

    @Autowired
    private OlapMenuService olapMenuService;

    @Test
    public void testMod(){

        OlapPermSubVO vo1 = new OlapPermSubVO();
        vo1.setPermId("P10455");
        vo1.setPermName("接口统计0别名");
        vo1.setStatus(1);
        OlapPermSubVO vo2 = new OlapPermSubVO();
        vo2.setPermId("P10283");
        vo2.setPermName("接口统计别名");
        vo2.setStatus(1);
        List<OlapPermSubVO> vos = Arrays.asList(vo1, vo2);
        olapMenuService.mod(vos);
    }

    @Test
    public void testInitMenu(){

        Assert.assertTrue(olapMenuService.initMenu());
    }
}
