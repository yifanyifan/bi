package com.stnts.bi.datamanagement.controller;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.service.SysService;
import com.stnts.bi.vo.DmVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: liang.zhang
 * @description: 提供给系统管理的接口
 * @date: 2021/5/26
 */
@RestController
@RequestMapping("sys")
@Api(value = "系统管理相关接口", tags = {"系统管理相关接口"})
@Slf4j
public class SysController {

    @Autowired
    private SysService sysService;

    @GetMapping("dms")
    public ResultEntity<List<DmVO>> dms(@RequestParam(required = false, name = "keyword") String keyword,
                                        @RequestParam(name = "departmentCode") List<String> departmentCodes){
        log.info("来自于系统管理的请求");
        return sysService.listDmVOList(keyword, departmentCodes);
    }

//    @PostMapping("dms")
//    public ResultEntity<List<DmVO>> dmsPost(List<String> departments){
//        log.info("来自于系统管理的请求");
//        return sysService.listDmVOList(keyword);
//    }
}
