package com.stnts.bi.datamanagement.module.cooperation.controller;


import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.util.JwtUtil;
import com.stnts.bi.datamanagement.util.SignUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * BI系统维护的行业分类表 前端控制器
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-03
 */
@RestController
@RequestMapping("/token/cooperation")
public class CooperationApiUsingTokenController {

    private final IndustryController industryController;

    private final CooperationBiController cooperationBiController;

    private final JwtUtil jwtUtil;

    public CooperationApiUsingTokenController(IndustryController industryController, SignUtil signUtil, CooperationBiController cooperationBiController, JwtUtil jwtUtil) {
        this.industryController = industryController;
        this.cooperationBiController = cooperationBiController;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/industry/tree")
    public ResultEntity industryTree(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Map<String, String> userInfo = jwtUtil.verify(token);
        return industryController.tree();
    }

    @GetMapping("/user/list")
    public ResultEntity queryUserTree(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Map<String, String> userInfo = jwtUtil.verify(token);
        return cooperationBiController.queryUserTree();
    }

}
