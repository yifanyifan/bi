package com.stnts.bi.datamanagement.module.cooperation.controller;


import cn.hutool.core.lang.Dict;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.util.SignUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * BI系统维护的行业分类表 前端控制器
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-03
 */
@RestController
@RequestMapping("/api/cooperation/industry")
public class IndustryApiController {

    private final IndustryController industryController;

    private final SignUtil signUtil;

    public IndustryApiController(IndustryController industryController, SignUtil signUtil) {
        this.industryController = industryController;
        this.signUtil = signUtil;
    }

    @GetMapping("/tree")
    public ResultEntity tree(String appId, Long timestamp, String sign) {
        signUtil.checkSign(appId, timestamp, sign, Dict.create());
        return industryController.tree();
    }

    @Deprecated
    @GetMapping("/parent/list")
    public ResultEntity listParentIndustry(String keyword, String appId, Long timestamp, String sign) {
        signUtil.checkSign(appId, timestamp, sign, Dict.create().set("keyword", keyword));
        return industryController.listParentIndustry(keyword);
    }

    @Deprecated
    @GetMapping("/child/list")
    public ResultEntity listChildIndustry(String keyword, String appId, Long timestamp, String sign) {
        signUtil.checkSign(appId, timestamp, sign, Dict.create().set("keyword", keyword));
        return industryController.listChildIndustry(keyword);
    }

}
