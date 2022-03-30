package com.stnts.bi.sys.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.ProductEntity;
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
 * @date: 2021/5/28
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestProductService {

    @Autowired
    private ProductService productService;

    @Test
    public void testListPage(){

        ResultEntity<Page<ProductEntity>> list = productService.list(1, null);
        System.out.println(JSON.toJSONString(list));
        Assert.assertNotNull(list.getData());
    }

    @Test
    public void testDel(){

        List<Integer> userIds = Arrays.asList(2239);
        ResultEntity<String> resultEntity = productService.delUsers(320000, userIds);
        Assert.assertEquals(resultEntity.getCode().toString(), "20000");
    }
}
