package com.stnts.bi.sys.controller;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.sys.common.OlapLog;
import com.stnts.bi.sys.service.OlapService;
import com.stnts.bi.sys.vos.olap.OlapPermPostVO;
import com.stnts.bi.sys.vos.olap.OlapPermVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: liang.zhang
 * @description:  与olap对接的接口
 * @date: 2021/1/6
 */
@Api(value = "BI-OLAP", tags = { "BI-OLAP" })
@RestController
@RequestMapping("olap")
public class OlapController {

    @Autowired
    private OlapService olapService;

    @OlapLog("OLAP:获取已发布")
    @ApiOperation("获取目录和已发布仪表盘")
    @ApiImplicitParams({
            @ApiImplicitParam(name="userId", required = true, dataType = "int", value = "用户ID", paramType = "param"),
            @ApiImplicitParam(name="sign", required = true, dataType = "string", value = "签名字符串", paramType = "param")
    })
    @GetMapping("list")
    public ResultEntity<List<OlapPermVO>> getPermList(@RequestParam(value = "userId") Integer userId,
                                                      @RequestParam(value = "sign") String sign){
        return olapService.getPermList(userId, sign);
    }

    @OlapLog(value = "OLAP:发布")
    @ApiOperation("发布")
    @PutMapping("mod")
    public ResultEntity<String> publish(@RequestBody OlapPermPostVO olapPermPostVO){

        return olapService.publish(olapPermPostVO);
    }
}
