package com.stnts.bi.sys.service;

import com.alibaba.fastjson.JSON;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.RoleEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2020/9/25
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class TestRoleService {

    @Autowired
    private RoleService roleService;

    @Test
    public void testListRoles(){

        ResultEntity<List<RoleEntity>> result = roleService.listRoles();
        Assert.assertEquals(result.getCode().intValue(), 20000);
    }
}
