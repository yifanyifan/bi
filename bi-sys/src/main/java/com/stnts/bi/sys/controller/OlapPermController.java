package com.stnts.bi.sys.controller;

import com.alibaba.fastjson.JSONArray;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.OlapPermEntity;
import com.stnts.bi.sys.common.OlapLog;
import com.stnts.bi.sys.service.OlapPermService;
import com.stnts.bi.sys.vos.olap.OlapPermItemVO;
import com.stnts.bi.sys.vos.olap.OlapPermModVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: liang.zhang
 * @description: olap用户权限设置的接口
 * @date: 2021/1/6
 */
@Api(value = "BI-OLAP权限管理", tags = {"BI-OLAP权限管理"})
@RestController
@RequestMapping("olap/perm")
public class OlapPermController {

    @Autowired
    private OlapPermService olapPermService;

    @ApiOperation("模块名称列表")
    @GetMapping("modules/{userId}")
    public ResultEntity<List<OlapPermEntity>> modules(@PathVariable(name = "userId") Integer userId) {
        return olapPermService.modules(userId);
    }

    @ApiOperation("用户树")
    @GetMapping("user/tree")
    public ResultEntity<JSONArray> userTree() {
        return olapPermService.userTree();
    }

    @ApiOperation("根据权限ID获取已有权限用户")
    @GetMapping("user/list/{permId}")
    @ApiImplicitParam(name = "permId", required = true, dataType = "string", paramType = "path")
    public ResultEntity<List<Integer>> loadUserByPermId(@PathVariable(name = "permId") String permId){
        return olapPermService.loadUserByPermId(permId);
    }

    @OlapLog("单个修改权限")
    @ApiOperation("单个修改权限")
    @PutMapping("mod/one")
    public ResultEntity<String> modOne(@RequestBody OlapPermModVO olapPermModVO) {
        return olapPermService.mod(olapPermModVO, "one");
    }

    @OlapLog("批量追加权限")
    @ApiOperation("批量追加权限")
    @PutMapping("mod/many")
    public ResultEntity<String> modMany(@RequestBody OlapPermModVO olapPermModVO) {
        return olapPermService.mod(olapPermModVO, "many");
    }

    @ApiOperation("权限列表")
    @GetMapping("list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "permId", required = true, dataType = "string", paramType = "param", value = "模块ID"),
            @ApiImplicitParam(name = "userId", required = true, dataType = "int", paramType = "param", value = "用户ID"),
            @ApiImplicitParam(name = "master", required = true, dataType = "int", paramType = "param", value = "是否菜单管理员1是0否")
    })
    public ResultEntity<List<OlapPermItemVO>> list(@RequestParam(required = false, name = "permId") String permId,
                                                   @RequestParam(required = false, name = "userId") Integer userId,
                                                   @RequestParam(required = false, name = "master", defaultValue = "0") Integer master) {
        return olapPermService.list(permId, userId, master);
    }

    @OlapLog("删除权限")
    @ApiOperation("删除权限")
    @ApiImplicitParam(name = "permId", dataType = "string", required = true, paramType = "path")
    @DeleteMapping("{permId}")
    public ResultEntity<String> del(@PathVariable(name="permId")  String permId){
        return olapPermService.del(permId);
    }
}
