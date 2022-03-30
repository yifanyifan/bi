package com.stnts.bi.gameop.mapper;

import com.stnts.bi.entity.gameop.DimCost;
import com.stnts.bi.mapper.gameop.DimCostMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/9/1
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class DimCostMapperTest {

    @Autowired
    private DimCostMapper dimCostMapper;

    @Test
    public void testInsert(){

        DimCost dimCost = DimCost.builder()
                .costDate(new Date())
                .pid("PID123456")
                .chargeModel("CPS")
                .source("斗鱼")
                .bookCost(123D)
                .realCost(456D)
                .build();

        Assert.assertTrue(dimCostMapper.insertOne(dimCost) > 0);
    }
}
