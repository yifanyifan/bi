package com.stnts.bi.datamanagement;

import com.alibaba.fastjson.JSON;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.service.SysService;
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
 * @date: 2021/6/2
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class SysServiceTest {

    @Autowired
    private SysService sysService;

    @Test
    public void listDmVoList(){

        String keyword = "余";
        List<String> departmentCodes = Arrays.asList("01.09", "01.05");
        ResultEntity<List<DmVO>> voList = sysService.listDmVOList(keyword, departmentCodes);
        System.out.println(JSON.toJSONString(voList));
        Assert.assertNotNull(voList.getData());
    }
}
