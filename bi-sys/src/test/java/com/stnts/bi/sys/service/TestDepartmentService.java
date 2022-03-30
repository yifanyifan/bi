package com.stnts.bi.sys.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.DepartmentEntity;
import com.stnts.bi.entity.sys.OrgEntity;
import com.stnts.bi.mapper.sys.OrgMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/5/27
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestDepartmentService {

    @Autowired
    private DepartmentService departmentService;

    @Test
    public void testList(){

        ResultEntity<Page<DepartmentEntity>> list = departmentService.list(null, 1);
        System.out.println(JSON.toJSONString(list));
        Assert.assertNotNull(list);
    }

    @Test
    public void testAddOrg(){

        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setOrgName("我的新组织");
        orgEntity.setCreatedBy(2239);

        ResultEntity<OrgEntity> resultEntity = departmentService.addOrg(orgEntity);
        Assert.assertNotNull(resultEntity.getData());
    }

    @Test
    public void testListOrg(){

        ResultEntity<List<OrgEntity>> resultEntity = departmentService.listOrg();
        Assert.assertNotNull(resultEntity.getData());
    }

    @Test
    public void testBindOrg(){

        DepartmentEntity departmentEntity = new DepartmentEntity();
        departmentEntity.setId(10);
        departmentEntity.setOrgId(1);

        ResultEntity<Boolean> resultEntity = departmentService.bindOrg(departmentEntity);
        Assert.assertTrue(resultEntity.getData());
    }

    @Test
    public void testDelOrg(){

        ResultEntity<Boolean> resultEntity = departmentService.delOrg(1);
        System.out.println(JSON.toJSONString(resultEntity));
        Assert.assertNotNull(resultEntity);
    }

    @Test
    public void testListDepartmentByOrgId(){

//        List<Integer> orgIds = Arrays.asList(1, 2, 3);
        List<Integer> orgIds = Collections.emptyList();
        List<DepartmentEntity> departments = departmentService.listDepartmentByOrgId(orgIds);
        Assert.assertNotNull(departments);
        System.out.println(JSON.toJSONString(departments));
    }
}
