package com.stnts.bi.sys.service;

import com.alibaba.fastjson.JSON;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sys.vos.olap.OlapPermPostVO;
import com.stnts.bi.sys.vos.olap.OlapPermVO;
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
 * @date: 2021/1/12
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestOlapService {

    @Autowired
    private OlapService olapService;

    @Test
    public void testGetPermList(){

        ResultEntity<List<OlapPermVO>> permList = olapService.getPermList(10498, "123");
        Assert.assertNotNull(permList.getData());
        System.out.println(JSON.toJSONString(permList.getData()));

    }

    @Test
    public void testPostPerm(){

        OlapPermPostVO postVO = new OlapPermPostVO();
        postVO.setUserId(2239);
        postVO.setRootId("2");
        OlapPermVO  m1 = initVO(91, 1, "插件目录1");
        OlapPermVO  m2 = initVO(92, 1, "插件目录2");
        OlapPermVO m1_p1 = initVO(91, 2, "插件页面1");
        OlapPermVO m1_p2 = initVO(92, 2, "插件页面2");
        OlapPermVO m1_p3 = initVO(93, 2, "插件页面3");
        OlapPermVO m2_p1 = initVO(94, 2, "插件页面5");

        List<OlapPermVO> subVOs = Arrays.asList(m1_p1, m1_p2, m1_p3);
        m1.setChildren(subVOs);
        List<OlapPermVO> _subVOs = Arrays.asList(m2_p1);
        m2.setChildren(_subVOs);
        List<OlapPermVO> vos = Arrays.asList(m1, m2);

        postVO.setPerms(vos);

        System.out.println(JSON.toJSONString(postVO));
        olapService.publish(postVO);
    }

    public OlapPermVO initVO(Integer olapPermId, Integer permType, String permName){
        OlapPermVO vo = new OlapPermVO();
        vo.setStatus(1);
        vo.setOlapPermId(olapPermId);
        vo.setPermType(permType);
        vo.setPermName(permName);
        return vo;
    }
}
