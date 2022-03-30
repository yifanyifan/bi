package com.stnts.bi.sys.controller;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.OlapPermEntity;
import com.stnts.bi.sys.common.OlapLog;
import com.stnts.bi.sys.service.OlapMenuService;
import com.stnts.bi.vo.OlapPermSubVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: liang.zhang
 * @description: olap权限菜单管理的接口
 * @date: 2021/1/6
 */
@Api(value = "BI-OLAP菜单管理", tags = {"BI-OLAP菜单管理"})
@RestController
@RequestMapping("olap/menu")
public class OlapMenuController {

    @Autowired
    private OlapMenuService olapMenuService;

    @ApiOperation("获取菜单树[所有]")
    @GetMapping("tree/all")
    @ApiImplicitParam(name = "permId", required = true, dataType = "string", paramType = "param")
    public ResultEntity<List<OlapPermEntity>> all(@RequestParam(required = false, name = "permId") String permId) {
        return olapMenuService.all(permId);
    }

    @ApiOperation("获取菜单树[仅自己拥有权限的]")
    @GetMapping("tree/self")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "permId", required = true, dataType = "string", paramType = "param"),
            @ApiImplicitParam(name = "userId", required = true, dataType = "int", paramType = "param")
    })
    public ResultEntity<List<OlapPermEntity>> self(@RequestParam(required = false, name = "permId") String permId,
                                                   @RequestParam(required = false, name = "userId") Integer userId) {
        return olapMenuService.self(permId, userId);
    }

    @OlapLog("导航管理")
    @ApiOperation("修改名称/调整顺序/删除菜单")
    @PutMapping("mod")
    public ResultEntity<String> mod(@RequestBody List<OlapPermSubVO> perms) {
        return olapMenuService.mod(perms);
    }
}
