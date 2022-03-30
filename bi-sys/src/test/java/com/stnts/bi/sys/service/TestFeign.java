package com.stnts.bi.sys.service;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sys.feign.DataManagementClient;
import com.stnts.bi.vo.DmVO;
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
 * @date: 2021/5/26
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFeign {

    @Autowired
    private DataManagementClient dataManagementClient;

    @Test
    public void testTest(){

        List<String> list = Arrays.asList("01.09", "01.05");
        ResultEntity<List<DmVO>> resultEntity = dataManagementClient.dms("余", list);
        System.out.println(resultEntity.getData());
        Assert.assertNotNull(resultEntity);
    }
}
