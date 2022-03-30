package com.stnts.bi.sys.service;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sys.vos.olap.OlapPermModVO;
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
 * @date: 2021/1/14
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestOlapPermService {

    @Autowired
    private OlapPermService olapPermService;

    @Test
    public void testMod(){

        List<String> perms = Arrays.asList("1_P_10283");
        List<Integer> users = Arrays.asList(1584, 1586);

        OlapPermModVO vo = new OlapPermModVO();
        vo.setPerms(perms);
        vo.setUsers(users);

        ResultEntity<String> many = olapPermService.mod(vo, "one");
        System.out.println(many.getCode());
    }
}
