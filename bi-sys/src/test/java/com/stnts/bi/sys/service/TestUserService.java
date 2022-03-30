package com.stnts.bi.sys.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.UserDmEntity;
import com.stnts.bi.entity.sys.UserEntity;
import com.stnts.bi.entity.sys.UserProductFocusEntity;
import com.stnts.bi.sys.params.UserOrgParam;
import com.stnts.bi.sys.params.UserRoleParam;
import com.stnts.bi.sys.vos.TreeVO;
import com.stnts.bi.sys.vos.UserRoleVO;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2020/9/25
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
public class TestUserService {

    @Autowired
    private UserService userService ;

    @Test
    public void testFindUserListByUserId(){

        List<Integer> roleIds = IntStream.of(1).boxed().collect(Collectors.toList());
        List<Integer> productIds = IntStream.of(130000, 320000).boxed().collect(Collectors.toList());
        ResultEntity<Page<UserEntity>> result = userService.findUserListByUserId(1, 2239, roleIds, productIds);
        Assert.assertEquals(result.getCode().intValue(), 20000);
    }

    @Test
    public void testFindUserListBySearch(){

        List<Integer> products = Arrays.asList(320000);
        ResultEntity<Page<UserEntity>> list = userService.findUserListBySearch(1, null, null, null, 10, products);
        Assert.assertNotNull(list.getData());
        System.out.println(JSON.toJSONString(list.getData().getRecords()));
    }

    @Test
    public void testModRole(){

        UserRoleParam role1 = new UserRoleParam();
        role1.setRoleId(1);
        role1.setProductIds("-9");
        role1.setProductNames("全部产品线");

        UserRoleParam role2 = new UserRoleParam();
        role2.setRoleId(5);
        role2.setProductIds("");
        role2.setProductNames("");

        ArrayList<UserRoleParam> userRoleParams = Lists.newArrayList(role1, role2);
        ResultEntity<String> resultEntity = userService.modRole(2239, userRoleParams);

        System.out.println(JSON.toJSONString(resultEntity));

        Assert.assertEquals(resultEntity.getCode().intValue(), 20000);
    }

    @Test
    public void testBindOrg(){

        UserOrgParam userOrgParam = new UserOrgParam();
        userOrgParam.setUserId(2239);
        userOrgParam.setOrgIds(Arrays.asList(1,10));
        ResultEntity<Boolean> booleanResultEntity = userService.bindOrg(userOrgParam);
        Assert.assertTrue(booleanResultEntity.getData());
    }

    @Test
    public void testUserRole(){

        ResultEntity<UserRoleVO> showRole = userService.showRole(2239);
        Assert.assertNotNull(showRole.getData());
        System.out.println(JSON.toJSONString(showRole.getData()));
    }

    @Test
    public void testSdk(){

        ResultEntity<List<TreeVO>> sdk = userService.sdk(2239);
        Assert.assertNotNull(sdk.getData());
        System.out.println(JSON.toJSONString(sdk.getData()));
    }

    @Test
    public void testlistDmByUserId(){

        ResultEntity<List<UserDmEntity>> result = userService.listDmByUserId(2239);
        Assert.assertNotNull(result.getData());
        System.out.println(JSON.toJSONString(result.getData()));
    }

    @Test
    public void testDeleteDmCcid(){

        ResultEntity<Boolean> entity = userService.delDmByCcid("CCID10002CPSfuE");
        Assert.assertTrue(entity.getData());
    }

    @Test
    public void testFocusProduct(){

        UserProductFocusEntity userProductFocusEntity = new UserProductFocusEntity();
        userProductFocusEntity.setUserId(2239);
        userProductFocusEntity.setProductId("320001");
        ResultEntity<Boolean> entity = userService.focusProduct(userProductFocusEntity);
        Assert.assertTrue(entity.getData());
    }

    @Test
    public void testGetFocusProduct(){

        ResultEntity<UserProductFocusEntity> product = userService.getFocusProduct(2239);
        System.out.println(product.getData().getProductId());
        Assert.assertNotNull(product.getData());
    }
}
