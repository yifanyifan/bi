package com.stnts.bi.sys.service;

import com.alibaba.fastjson.JSON;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.SdkDataLevelEntity;
import com.stnts.bi.sys.vos.DataLevelVO;
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
 * @date: 2021/5/28
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestSdkDataLevelService {

    @Autowired
    private SdkDataLevelService sdkDataLevelService;

    @Test
    public void testList(){

        ResultEntity<List<SdkDataLevelEntity>> list = sdkDataLevelService.list("320000");
        System.out.println(JSON.toJSONString(list));
        Assert.assertNotNull(list);
    }

    @Test
    public void testAdd(){

        SdkDataLevelEntity sdkDataLevelEntity = new SdkDataLevelEntity()
                .setLevelName("第一层第一个节点子节点")
                .setCreatedBy(2239)
                .setIdx(1)
                .setType(1)
                .setPid(1)
                .setPath(".1");
        ResultEntity<SdkDataLevelEntity> entity = sdkDataLevelService.add(sdkDataLevelEntity);
        Assert.assertNotNull(entity.getData().getLevelId());
    }

    @Test
    public void testRename(){

        SdkDataLevelEntity sdkDataLevelEntity = new SdkDataLevelEntity()
                .setLevelId(1)
                .setLevelName("11");
        ResultEntity<SdkDataLevelEntity> entity = sdkDataLevelService.rename(sdkDataLevelEntity);
        System.out.println(JSON.toJSONString(entity));
        Assert.assertNotNull(entity);
    }

    @Test
    public void testDrag(){

        SdkDataLevelEntity sdkDataLevelEntity = new SdkDataLevelEntity();
        sdkDataLevelEntity.setLevelId(2239);
        sdkDataLevelEntity.setPath(".2.3");
        sdkDataLevelEntity.setPid(3);
        ResultEntity<SdkDataLevelEntity> drag = sdkDataLevelService.drag(sdkDataLevelEntity);
        Assert.assertNotNull(drag.getData());
    }
}
