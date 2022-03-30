package com.stnts.bi.datamanagement.module.channel.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.config.DataManagementConfig;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelCooperation;
import com.stnts.bi.datamanagement.module.channel.param.ChannelCooperationPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelCooperationService;
import com.stnts.bi.datamanagement.module.channel.vo.AgentVO;
import com.stnts.bi.datamanagement.module.channel.vo.ChannelVO;
import com.stnts.bi.datamanagement.module.channel.vo.DepartmentVO;
import com.stnts.bi.entity.common.PageEntity;
import com.stnts.bi.validator.groups.Add;
import com.stnts.bi.validator.groups.Update;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 渠道合作 控制器
 *
 * @author 刘天元
 * @since 2021-02-03
 */
@Slf4j
@RestController
@RequestMapping("/channelCooperation")
@Api(value = "渠道合作API", tags = {"渠道合作"})
public class ChannelCooperationController {

    @Autowired
    private ChannelCooperationService channelCooperationService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private DataManagementConfig dataManagementConfig;

    @GetMapping("/indicatorsEnum")
    @ApiOperation(value = "结算指标1.收入2.利润3.注册4.激活 By Yf")
    public ResultEntity departmentList() {
        Map map = new HashMap();
        try {
            Map j1 = new HashMap();
            j1.put("1", "收入");
            j1.put("2", "利润");
            Map j2 = new HashMap();
            j2.put("3", "注册");
            j2.put("4", "激活");

            map.put("CPS", j1);
            map.put("CPA", j2);
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }
        return ResultEntity.success(map);
    }

    @GetMapping("/search/all")
    @ApiOperation(value = "查询-搜索框全部集合 By Yf")
    public ResultEntity searchAll(ChannelCooperationPageParam param, HttpServletRequest request) {
        Map<String, Object> mapAll = channelCooperationService.searchAll(param, request);

        return ResultEntity.success(mapAll);
    }

    /**
     * 添加渠道合作
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加渠道合作", response = ResultEntity.class)
    public ResultEntity<Boolean> addChannelCooperation(@Validated(Add.class) @RequestBody ChannelCooperation channelCooperation, HttpServletRequest request) throws Exception {
        if (StringUtils.isBlank(channelCooperation.getDataSource())) {
            channelCooperation.setDataSource("BI");
        }
        String ccid = channelCooperationService.saveChannelCooperation(channelCooperation, request);
        return ResultEntity.success(StrUtil.isNotEmpty(ccid));
    }

    /**
     * 修改渠道合作
     */
    @PostMapping("/update")
    @ApiOperation(value = "修改渠道合作", response = ResultEntity.class)
    public ResultEntity<Boolean> updateChannelCooperation(@Validated(Update.class) @RequestBody ChannelCooperation channelCooperation, HttpServletRequest request) throws Exception {
        boolean flag = channelCooperationService.updateChannelCooperation(channelCooperation, request);
        return ResultEntity.success(flag);
    }

    /**
     * 删除渠道合作
     */
    @PostMapping("/delete/{id}")
    @ApiOperation(value = "删除渠道合作", response = ResultEntity.class)
    public ResultEntity<Boolean> deleteChannelCooperation(@PathVariable("id") Long id) throws Exception {
        boolean flag = channelCooperationService.deleteChannelCooperation(id);
        return ResultEntity.success(flag);
    }

    /**
     * 获取渠道合作详情
     */
    @GetMapping("/info/{id}")
    @ApiOperation(value = "渠道合作详情", response = ChannelCooperation.class)
    public ResultEntity<ChannelCooperation> getChannelCooperation(@PathVariable("id") Long id, HttpServletRequest request) throws Exception {
        ChannelCooperation channelCooperation = channelCooperationService.getWithId(id, request);
        return ResultEntity.success(channelCooperation);
    }

    @GetMapping("/preEdit/{ccid}")
    @ApiOperation(value = "渠道合作详情[编辑之前获取详情用,有权限验证]", response = ChannelCooperation.class)
    public ResultEntity<ChannelCooperation> getChannelCooperationPreEdit(@PathVariable("ccid") String ccid, HttpServletRequest request) throws Exception {
        ChannelCooperation channelCooperation = channelCooperationService.info(ccid, true, request);
        return ResultEntity.success(channelCooperation);
    }

    /**
     * 获取渠道合作详情
     */
    @GetMapping("/infoExt/{ccid}")
    @ApiOperation(value = "渠道合作详情[CCID详情]", response = ChannelCooperation.class)
    public ResultEntity<ChannelCooperation> getChannelCooperationExt(@PathVariable("ccid") String ccid, HttpServletRequest request) throws Exception {
        ChannelCooperation channelCooperation = channelCooperationService.info(ccid, false, request);
        return ResultEntity.success(channelCooperation);
    }

    /**
     * 渠道合作分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "渠道合作分页列表", response = ChannelCooperation.class)
    public ResultEntity<PageEntity<ChannelCooperation>> getChannelCooperationPageList(@Validated @RequestBody ChannelCooperationPageParam channelCooperationPageParam,
                                                                                      HttpServletRequest request) throws Exception {
        PageEntity<ChannelCooperation> paging = channelCooperationService.getChannelCooperationPageList(channelCooperationPageParam, request);
        return ResultEntity.success(paging);
    }

    /**
     * 渠道合作列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "渠道合作列表", response = ChannelCooperation.class)
    @ApiOperationSupport(ignoreParameters = {"channelCooperationPageParam.pageIndex", "channelCooperationPageParam.pageSorts", "channelCooperationPageParam.pageSize"})
    public ResultEntity<List<ChannelCooperation>> getChannelCooperationList(@Validated @RequestBody ChannelCooperationPageParam channelCooperationPageParam,
                                                                            HttpServletRequest request) throws Exception {
        List<ChannelCooperation> list = channelCooperationService.getChannelCooperationList(channelCooperationPageParam, request);
        return ResultEntity.success(list);
    }

    @PostMapping("/getPageListExt")
    @ApiOperation(value = "渠道合作CCID分页列表扩展")
    public ResultEntity<PageEntity<ChannelCooperation>> getChannelCooperationPageListExt(@Validated @RequestBody ChannelCooperationPageParam channelCooperationPageParam, HttpServletRequest request) throws Exception {
        PageEntity<ChannelCooperation> paging = channelCooperationService.getChannelCooperationPageListExt(channelCooperationPageParam, request);

        return ResultEntity.success(paging);
    }

    @PostMapping("/getDepartmentCodeOnly")
    @ApiOperation(value = "返回多个CCID的唯一部门CODE")
    public ResultEntity<Map<String, String>> getDepartmentCodeOnly(@Validated @RequestBody ChannelCooperationPageParam channelCooperationPageParam, HttpServletRequest request) throws Exception {
        Map<String, String> res = channelCooperationService.getDepartmentCodeOnly(channelCooperationPageParam);

        return ResultEntity.success(res);
    }

    @PostMapping("/updateBusinessDictBatch")
    @ApiOperation(value = "批量修改业务分类")
    public ResultEntity updateBusinessDictBatch(@RequestBody ChannelCooperationPageParam channelCooperationPageParam, HttpServletRequest request) throws Exception {
        channelCooperationService.updateBusinessDictBatch(channelCooperationPageParam, request);

        return ResultEntity.success(true);
    }

    @PostMapping("listDepartment")
    @ApiOperation("搜索条件:获取部门列表")
    @ApiOperationSupport(ignoreParameters = {"rootLevelBusiness", "firstLevelBusiness", "secondLevelBusiness", "thirdLevelBusiness", "subChannelId", "chargeRule", "departmentCode", "pageIndex", "pageSize", "keyword"})
    public ResultEntity<List<DepartmentVO>> listDepartment(@Validated @RequestBody ChannelCooperationPageParam channelCooperationPageParam) {
        return channelCooperationService.listDepartment(channelCooperationPageParam);
    }

    @PostMapping("listAgent")
    @ApiOperation("搜索条件:获取公司列表")
    @ApiOperationSupport(ignoreParameters = {"rootLevelBusiness", "firstLevelBusiness", "secondLevelBusiness", "thirdLevelBusiness", "subChannelId", "chargeRule", "agentId", "agentName", "pageIndex", "pageSize", "keyword"})
    public ResultEntity<List<AgentVO>> listAgent(@Validated @RequestBody ChannelCooperationPageParam channelCooperationPageParam) {
        return channelCooperationService.listAgent(channelCooperationPageParam);
    }

    @PostMapping("listChannel")
    @ApiOperation("搜索条件:获取渠道列表")
    @ApiOperationSupport(ignoreParameters = {"rootLevelBusiness", "firstLevelBusiness", "secondLevelBusiness", "thirdLevelBusiness", "subChannelId", "chargeRule", "channelId", "channelName", "pageIndex", "pageSize", "keyword"})
    public ResultEntity<List<ChannelVO>> listChannel(@Validated @RequestBody ChannelCooperationPageParam channelCooperationPageParam) {
        return channelCooperationService.listChannel(channelCooperationPageParam);
    }
}

