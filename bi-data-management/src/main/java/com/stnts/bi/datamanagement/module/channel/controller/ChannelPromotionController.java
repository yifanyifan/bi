package com.stnts.bi.datamanagement.module.channel.controller;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.config.DataManagementConfig;
import com.stnts.bi.datamanagement.exception.BusinessException;
import com.stnts.bi.datamanagement.module.channel.entity.Channel;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelCooperation;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelPromotion;
import com.stnts.bi.datamanagement.module.channel.param.ChannelPromotionPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelChildService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelPromotionService;
import com.stnts.bi.datamanagement.module.channel.vo.*;
import com.stnts.bi.datamanagement.util.DozerUtil;
import com.stnts.bi.datamanagement.util.ExcelUtils;
import com.stnts.bi.datamanagement.util.SignUtils;
import com.stnts.bi.entity.common.PageEntity;
import com.stnts.bi.validator.groups.Add;
import com.stnts.bi.validator.groups.Update;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 渠道推广 控制器
 *
 * @author 刘天元
 * @since 2021-02-04
 */
@Slf4j
@RestController
@RequestMapping("/channelPromotion")
@Api(value = "渠道推广API", tags = {"渠道推广"})
public class ChannelPromotionController {

    @Autowired
    private ChannelPromotionService channelPromotionService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private DataManagementConfig dataManagementConfig;

    @Autowired
    private ChannelChildService channelChildService;

    @GetMapping("/search/all")
    @ApiOperation(value = "查询-搜索框全部集合 By Yf")
    public ResultEntity searchList(ChannelPromotionPageParam param, HttpServletRequest request) {
        Map<String, Object> mapAll = channelPromotionService.searchList(param, request);

        return ResultEntity.success(mapAll);
    }

    /**
     * 添加渠道推广
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加渠道推广", response = ResultEntity.class)
    @ApiOperationSupport(ignoreParameters = {"channelPromotion.channelNameSettlement"})
    public ResultEntity<String> addChannelPromotion(@Validated(Add.class) @RequestBody ChannelPromotion channelPromotion) throws Exception {
        //页面来源默认BI
        if (StringUtils.isBlank(channelPromotion.getDataSource())) {
            channelPromotion.setDataSource("BI");
        }

        //需要返回子渠道ID
        Map<Object, Object> map = channelPromotionService.saveChannelPromotion(channelPromotion);
        Object obj = map.get("subChannelId");
        return ResultEntity.success(String.valueOf(obj));
    }

    /**
     * 修改渠道推广
     */
    @PostMapping("/update")
    @ApiOperation(value = "修改渠道推广", response = ResultEntity.class)
    public ResultEntity<Boolean> updateChannelPromotion(@Validated(Update.class) @RequestBody ChannelPromotion channelPromotion) throws Exception {
        boolean flag = channelPromotionService.updateChannelPromotion(channelPromotion);
        return ResultEntity.success(flag);
    }

    @PostMapping("/updateSub")
    @ApiOperation(value = "批量修改【小字段：负责人、别名】", response = ResultEntity.class)
    public ResultEntity<Boolean> updateChannelPromotionSub(@RequestBody ChannelPromotion channelPromotion) throws Exception {
        boolean flag = channelPromotionService.updateChannelPromotionSub(channelPromotion);
        return ResultEntity.success(flag);
    }

    @PostMapping("/updateSubReplace")
    @ApiOperationSupport(includeParameters = {"channelPromotion.idList", "channelPromotion.replaceSource", "channelPromotion.replaceTarget"})
    @ApiOperation(value = "批量替换【别名】", response = ResultEntity.class)
    public ResultEntity<Boolean> updateSubReplace(@RequestBody ChannelPromotion channelPromotion) throws Exception {
        boolean flag = channelPromotionService.updateSubReplace(channelPromotion);
        return ResultEntity.success(flag);
    }

    /**
     * 删除渠道推广
     */
    @PostMapping("/delete/{id}")
    @ApiOperation(value = "删除渠道推广", response = ResultEntity.class)
    public ResultEntity<Boolean> deleteChannelPromotion(@PathVariable("id") Long id) throws Exception {
        boolean flag = channelPromotionService.deleteChannelPromotion(id);
        return ResultEntity.success(flag);
    }

    /**
     * 获取渠道推广详情
     */
    @GetMapping("/info/{id}")
    @ApiOperation(value = "渠道推广详情", response = ChannelPromotion.class)
    public ResultEntity<ChannelPromotion> getChannelPromotion(@PathVariable("id") Long id) throws Exception {
        ChannelPromotion channelPromotion = channelPromotionService.getById(id);
        return ResultEntity.success(channelPromotion);
    }

    /**
     * 获取渠道推广详情
     */
    @GetMapping("/infoExt/{id}")
    @ApiOperation(value = "渠道推广详情-扩展", response = ChannelPromotionVO.class)
    public ResultEntity<ChannelPromotionVO> getChannelPromotionExt(@PathVariable("id") Long id) throws Exception {
        ChannelPromotionVO channelPromotion = channelPromotionService.info(id);
        return ResultEntity.success(channelPromotion);
    }

    /**
     * 渠道推广分页列表
     */
    @PostMapping("/getPageListExt")
    @ApiOperation(value = "渠道推广分页列表-扩展")
    public ResultEntity<PageEntity<ChannelPromotionVO>> getPidPageList(@Validated @RequestBody ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest request) throws Exception {
        PageEntity<ChannelPromotionVO> paging = channelPromotionService.getPidPageList(channelPromotionPageParam, request);

        return ResultEntity.success(paging);
    }

    /**
     * 渠道推广列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "渠道推广列表", response = ChannelPromotion.class)
    @ApiOperationSupport(ignoreParameters = {"channelPromotionPageParam.pageIndex", "channelPromotionPageParam.pageSorts", "channelPromotionPageParam.pageSize"})
    public ResultEntity<List<ChannelPromotion>> getChannelPromotionList(@Validated @RequestBody ChannelPromotionPageParam channelPromotionPageParam,
                                                                        HttpServletRequest request) throws Exception {
        List<ChannelPromotion> list = channelPromotionService.getChannelPromotionList(channelPromotionPageParam, request);
        return ResultEntity.success(list);
    }

    @PostMapping("/getAppPageList")
    @ApiOperation(value = "根据CCID查找产品列表")
    public ResultEntity<PageEntity<AppVO>> getProductAndAppByCcid(@Validated @RequestBody ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest request) throws Exception {
        PageEntity<AppVO> productAndAppByCcid = channelPromotionService.getProductAndAppByCcid(channelPromotionPageParam, request);
        return ResultEntity.success(productAndAppByCcid);
    }

    @PostMapping("/getAppPageListNoPage")
    @ApiOperation(value = "根据CCID查找产品列表（不分页）")
    public ResultEntity<List<AppVO>> getAppPageListNoPage(@Validated @RequestBody ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest request) throws Exception {
        List<AppVO> productAndAppByCcid = channelPromotionService.getAppPageListNoPage(channelPromotionPageParam, request);
        return ResultEntity.success(productAndAppByCcid);
    }

    @PostMapping("listDepartment")
    @ApiOperation("搜索条件:部门列表")
    @ApiOperationSupport(ignoreParameters = {"pidList", "productId", "applicationId", "mediumId", "pid", "ccid", "departmentCode", "pageIndex", "pageSize"})
    public ResultEntity<List<DepartmentVO>> listDepartment(@Validated @RequestBody ChannelPromotionPageParam channelPromotionPageParam) {
        return channelPromotionService.listDepartment(channelPromotionPageParam);
    }

    @PostMapping("listCompany")
    @ApiOperation("搜索条件:公司列表")
    @ApiOperationSupport(ignoreParameters = {"pidList", "productId", "applicationId", "mediumId", "pid", "ccid", "agentId", "pageIndex", "pageSize"})
    public ResultEntity<List<AgentVO>> listCompany(@Validated @RequestBody ChannelPromotionPageParam channelPromotionPageParam) {
        return channelPromotionService.listCompany(channelPromotionPageParam);
    }

    @PostMapping("listChannel")
    @ApiOperation("搜索条件:渠道列表")
    @ApiOperationSupport(ignoreParameters = {"pidList", "productId", "applicationId", "mediumId", "pid", "ccid", "channelId", "pageIndex", "pageSize"})
    public ResultEntity<List<ChannelVO>> listChannel(@Validated @RequestBody ChannelPromotionPageParam channelPromotionPageParam) {
        return channelPromotionService.listChannel(channelPromotionPageParam);
    }

    @PostMapping("listSubChannel")
    @ApiOperation("搜索条件:子渠道列表")
    @ApiOperationSupport(ignoreParameters = {"pidList", "productId", "applicationId", "mediumId", "pid", "ccid", "subChannelId", "pageIndex", "pageSize"})
    public ResultEntity<List<SubChannelVO>> listSubChannel(@Validated @RequestBody ChannelPromotionPageParam channelPromotionPageParam) {
        return channelPromotionService.listSubChannel(channelPromotionPageParam);
    }

    @GetMapping("sign/getPidBusinessInfo")
    @ApiIgnore
    public ResultEntity<PidVO> getPidBusinessInfo(@RequestParam String appId, @RequestParam String sign, @RequestParam Long timestamp, @RequestParam String pid) {

        SignUtils.check(appId, sign, timestamp, pid);
        return channelPromotionService.getPidBusinessInfo(pid);
    }

    /**
     * 当前CCID对应部门下的所有CCID对应的渠道列表
     */
    @PostMapping("/migrationProductList")
    @ApiOperation("迁移-目标渠道列表")
    public ResultEntity<List<Channel>> migrationProductList(@RequestBody ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest request) throws Exception {

        List<Channel> channelList = channelPromotionService.migrationChannelList(channelPromotionPageParam, request);

        return ResultEntity.success(channelList);
    }

    /**
     * 当前CCID对应部门下的所有CCID
     */
    @PostMapping("/migrationCCIDList")
    @ApiOperation("迁移-目标CCID列表")
    public ResultEntity<List<ChannelCooperation>> migrationCCIDList(@RequestBody ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest request) throws Exception {

        List<ChannelCooperation> channelList = channelPromotionService.migrationCCIDList(channelPromotionPageParam, request);

        return ResultEntity.success(channelList);
    }

    @PostMapping("/migration")
    @ApiOperation("迁移")
    public ResultEntity migration(@RequestBody ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest request) throws Exception {
        if (StringUtils.isBlank(channelPromotionPageParam.getDataSource())) {
            channelPromotionPageParam.setDataSource("BI");
        }
        channelPromotionService.migration(channelPromotionPageParam, request);
        return ResultEntity.success("OK");
    }

    @GetMapping("/exportBatchMoban")
    @ApiOperation("导出批量添加渠道推广Excel模板")
    public void exportBatchMoban(HttpServletResponse response) throws Exception {
        String[] columNames = {"子渠道名称", "推广位", "产品CODE", "产品名称", "应用", "推广媒介(多选,号分隔)", "拓展字段", "计费别名", "内结CCID"};
        String[] columKeys = {"subChannelName", "ppName", "productCode", "productName", "applicationName", "mediumName", "extra", "pidAlias", "ccidSettlement"};
        List<Map> list = new ArrayList<Map>();
        Map map = new HashMap<>();
        map.put("subChannelName", "");
        map.put("ppName", "");
        map.put("productCode", "");
        map.put("productName", "");
        map.put("applicationName", "");
        map.put("mediumName", "");
        map.put("extra", "");
        map.put("pidAlias", "");
        map.put("ccidSettlement", "");
        list.add(map);
        ExcelUtils.export(response, "渠道推广批量导入模板", list, columKeys, columNames);
    }

    @PostMapping("/addBatch")
    @ApiOperation(value = "批量上传渠道推广", response = ResultEntity.class)
    public ResultEntity<Boolean> addBatch(ChannelPromotion channelPromotion, @RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {

        String[] columNames = {"subChannelName", "ppName", "productCode", "productName", "applicationName", "mediumName", "extra", "pidAlias", "ccidSettlement"};
        List<Map<String, Object>> list = ExcelUtils.leading(file, columNames, 1);

        if (list.size() == 0) {
            throw new BusinessException("上传表格为空");
        }

        List<ChannelPromotion> channelPromotionList = new ArrayList<ChannelPromotion>();
        for (Map<String, Object> obj : list) {
            ChannelPromotion channelPromotionExcel = JSON.parseObject(JSON.toJSONString(obj), ChannelPromotion.class);
            BeanUtils.copyProperties(channelPromotion, channelPromotionExcel, DozerUtil.getNullPropertyNames(channelPromotion));
            if (StringUtils.isBlank(channelPromotionExcel.getPidAlias())) {
                //防止最后1个空行
                continue;
            }
            //页面来源默认BI
            if (StringUtils.isBlank(channelPromotionExcel.getDataSource())) {
                channelPromotionExcel.setDataSource("BI");
            }
            channelPromotionList.add(channelPromotionExcel);
        }

        //需要返回子渠道ID
        Boolean b = channelPromotionService.saveChannelPromotionBatch(channelPromotionList, request);

        return ResultEntity.success(b);
    }

    @PostMapping("/addBatchIn")
    @ApiOperation(value = "批量录入渠道推广", response = ResultEntity.class)
    public ResultEntity<Boolean> addBatchIn(@RequestBody ChannelPromotionVO channelPromotionVO, HttpServletRequest request) throws Exception {
        List<ChannelPromotion> channelPromotionList = new ArrayList<ChannelPromotion>();

        List<ChannelPromotion> channelPromotionListByRow = channelPromotionVO.getChannelPromotionList();
        if (channelPromotionListByRow.size() > 10) {
            throw new BusinessException("批量录入仅支持10条内");
        }

        for (ChannelPromotion channelPromotion : channelPromotionListByRow) {
            ChannelPromotion channelPromotionNew = new ChannelPromotion();

            BeanUtils.copyProperties(channelPromotionVO, channelPromotionNew, DozerUtil.getNullPropertyNames(channelPromotionVO));
            BeanUtils.copyProperties(channelPromotion, channelPromotionNew, DozerUtil.getNullPropertyNames(channelPromotion));

            //页面来源默认BI
            if (StringUtils.isBlank(channelPromotionNew.getDataSource())) {
                channelPromotionNew.setDataSource("BI");
            }
            channelPromotionList.add(channelPromotionNew);
        }

        //需要返回子渠道ID
        Boolean b = channelPromotionService.saveChannelPromotionBatch(channelPromotionList, request);

        return ResultEntity.success(b);
    }

    @GetMapping("/exportPID")
    @ApiOperation(value = "导出")
    public void exportPID(ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<ChannelPromotionVO> paging = channelPromotionService.getPidPageListToExcel(channelPromotionPageParam, request);

        String[] columNames = {"PID", "计费别名", "CCID", "渠道名称", "子渠道", "部门名称", "推广位", "产品名称/应用", "推广媒介", "拓展字段", "负责人", "有效期", "最后修改时间"};
        String[] columKeys = {"pid", "pidAlias", "ccid", "channelName", "subChannelName", "departmentName", "promotionPositionName", "productNameAndApplicationName", "mediumName", "extra", "username", "checkDateStr", "updateTime"};
        ExcelUtils.export(response, "渠道推广PID列表导出" + DateUtil.format(new Date(), "yyyyMMddHHmmss"), paging, columKeys, columNames);
    }

    @PostMapping("/settlementChannelList")
    @ApiOperation(value = "内结渠道下拉（部门传产品部门）")
    @ApiOperationSupport(ignoreParameters = {"channelPromotionPageParam.channelId", "channelPromotionPageParam.settlementType", "channelPromotionPageParam.agentId", "channelPromotionPageParam.applicationId", "channelPromotionPageParam.applicationName", "channelPromotionPageParam.channelApplicationList", "channelPromotionPageParam.channelName", "channelPromotionPageParam.checkEndDate", "channelPromotionPageParam.checkStartDate", "channelPromotionPageParam.departmentCodeAllList", "channelPromotionPageParam.departmentName", "channelPromotionPageParam.extra", "channelPromotionPageParam.keyword", "channelPromotionPageParam.mapAll", "channelPromotionPageParam.mediumId", "channelPromotionPageParam.mediumName", "channelPromotionPageParam.orders", "channelPromotionPageParam.pageIndex", "channelPromotionPageParam.pageSize", "channelPromotionPageParam.pid", "channelPromotionPageParam.pidAlias", "channelPromotionPageParam.pidList", "channelPromotionPageParam.prodApp", "channelPromotionPageParam.productApplicatioName", "channelPromotionPageParam.productCode", "channelPromotionPageParam.productId", "channelPromotionPageParam.productName", "channelPromotionPageParam.productNameAndApplicationName", "channelPromotionPageParam.promoteIdList", "channelPromotionPageParam.subChannelId", "channelPromotionPageParam.subChannelName", "channelPromotionPageParam.targetCCId", "channelPromotionPageParam.targetChannelId", "channelPromotionPageParam.targetCompanyId", "channelPromotionPageParam.username", "channelPromotionPageParam.usernameName"})
    public ResultEntity<List<Channel>> settlementChannelList(@RequestBody ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest httpServletRequest) throws Exception {
        // 展示所有有权限的内结渠道 -- 丁勤霞20211125
        List<Channel> channelList = channelPromotionService.settlementChannelList(channelPromotionPageParam, httpServletRequest);

        return ResultEntity.success(channelList);
    }

    @PostMapping("/settlementCCIDList")
    @ApiOperation(value = "内结CCID下拉（部门传产品部门）")
    @ApiOperationSupport(ignoreParameters = {"channelPromotionPageParam.ccid", "channelPromotionPageParam.settlementType", "channelPromotionPageParam.agentId", "channelPromotionPageParam.applicationId", "channelPromotionPageParam.applicationName", "channelPromotionPageParam.channelApplicationList", "channelPromotionPageParam.channelName", "channelPromotionPageParam.checkEndDate", "channelPromotionPageParam.checkStartDate", "channelPromotionPageParam.departmentCodeAllList", "channelPromotionPageParam.departmentName", "channelPromotionPageParam.extra", "channelPromotionPageParam.keyword", "channelPromotionPageParam.mapAll", "channelPromotionPageParam.mediumId", "channelPromotionPageParam.mediumName", "channelPromotionPageParam.orders", "channelPromotionPageParam.pageIndex", "channelPromotionPageParam.pageSize", "channelPromotionPageParam.pid", "channelPromotionPageParam.pidAlias", "channelPromotionPageParam.pidList", "channelPromotionPageParam.prodApp", "channelPromotionPageParam.productApplicatioName", "channelPromotionPageParam.productCode", "channelPromotionPageParam.productId", "channelPromotionPageParam.productName", "channelPromotionPageParam.productNameAndApplicationName", "channelPromotionPageParam.promoteIdList", "channelPromotionPageParam.subChannelId", "channelPromotionPageParam.subChannelName", "channelPromotionPageParam.targetCCId", "channelPromotionPageParam.targetChannelId", "channelPromotionPageParam.targetCompanyId", "channelPromotionPageParam.username", "channelPromotionPageParam.usernameName"})
    public ResultEntity<List<ChannelCooperation>> settlementCCIDList(@RequestBody ChannelPromotionPageParam channelPromotionPageParam) throws Exception {
        //产品对应部门下渠道标记为内结的所有渠道 ，关联的CCID
        List<ChannelCooperation> channelList = channelPromotionService.settlementCCIDList(channelPromotionPageParam);

        return ResultEntity.success(channelList);
    }


    @PostMapping("/settlementChannelListBatch")
    @ApiOperation(value = "批量修改-内结渠道下拉")
    @ApiOperationSupport(includeParameters = {"channelPromotionPageParam.pidList", "channelPromotionPageParam.ccid"})
    public ResultEntity<List<Channel>> settlementChannelListBatch(@RequestBody ChannelPromotionPageParam channelPromotionPageParam, HttpServletRequest httpServletRequest) throws Exception {
        // 指定多个PID对应产品对应部门下渠道标记为内结的所有渠道
        List<Channel> channelList = channelPromotionService.settlementChannelListByBatch(channelPromotionPageParam, httpServletRequest);

        return ResultEntity.success(channelList);
    }

    @PostMapping("/settlementCCIDListBatch")
    @ApiOperation(value = "批量修改-内结CCID下拉")
    @ApiOperationSupport(includeParameters = {"channelPromotionPageParam.pidList", "channelPromotionPageParam.channelId"})
    public ResultEntity<List<ChannelCooperation>> settlementCCIDListBatch(@RequestBody ChannelPromotionPageParam channelPromotionPageParam) throws Exception {
        //产品对应部门下渠道标记为内结的所有渠道 ，关联的CCID
        List<ChannelCooperation> channelList = channelPromotionService.settlementCCIDListByBatch(channelPromotionPageParam);

        return ResultEntity.success(channelList);
    }

    @PostMapping("/settlementUpdateBatch")
    @ApiOperation(value = "批量修改-确认修改内结CCID")
    @ApiOperationSupport(includeParameters = {"channelPromotionPageParam.pidList", "channelPromotionPageParam.ccid"})
    public ResultEntity<Map> settlementUpdateBatch(@RequestBody ChannelPromotionPageParam channelPromotionPageParam) throws Exception {
        //产品对应部门下渠道标记为内结的所有渠道 ，关联的CCID
        Map map = channelPromotionService.settlementUpdateBatch(channelPromotionPageParam);

        return ResultEntity.success(map);
    }


}

