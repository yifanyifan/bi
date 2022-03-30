package com.stnts.bi.datamanagement.module.channel.controller;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.config.DataManagementConfig;
import com.stnts.bi.datamanagement.module.channel.entity.Channel;
import com.stnts.bi.datamanagement.module.channel.entity.ChannelChild;
import com.stnts.bi.datamanagement.module.channel.param.ChannelChildPageParam;
import com.stnts.bi.datamanagement.module.channel.param.ChannelPageParam;
import com.stnts.bi.datamanagement.module.channel.service.ChannelChildService;
import com.stnts.bi.datamanagement.module.channel.service.ChannelService;
import com.stnts.bi.datamanagement.module.cooperation.entity.Cooperation;
import com.stnts.bi.datamanagement.module.cooperation.service.CooperationService;
import com.stnts.bi.datamanagement.module.exportdata.dataenum.ChannelTypeEnum;
import com.stnts.bi.datamanagement.module.exportdata.dataenum.SecretTypeEnum;
import com.stnts.bi.datamanagement.module.exportdata.dataenum.SettlementTypeEnum;
import com.stnts.bi.entity.common.PageEntity;
import com.stnts.bi.validator.groups.Add;
import com.stnts.bi.validator.groups.Update;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 渠道
 */
@Slf4j
@RestController
@RequestMapping("/channel")
@Api(value = "渠道API", tags = {"渠道"})
public class ChannelController {

    @Autowired
    private ChannelService channelService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private DataManagementConfig dataManagementConfig;
    @Autowired
    private CooperationService cooperationService;
    @Autowired
    private ChannelChildService channelChildService;

    @GetMapping("/search/all")
    @ApiOperation(value = "查询-搜索框全部集合 By Yf")
    public ResultEntity searchList(@ApiParam(name = "departmentCode", value = "部门Code") String departmentCode,
                                   @ApiParam(name = "companyId", value = "公司ID") String companyId,
                                   @ApiParam(name = "channelId", value = "渠道ID") Long channelId,
                                   @ApiParam(name = "secretType", value = "保密类型") Integer secretType,
                                   HttpServletRequest request) {
        Map<String, Object> mapAll = channelService.searchList(departmentCode, companyId, channelId, secretType, request);

        return ResultEntity.success(mapAll);
    }

    /**
     * 添加渠道
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加渠道", response = ResultEntity.class)
    @ApiOperationSupport(ignoreParameters = {"channelId", "createTime", "updateTime"})
    public ResultEntity<Boolean> addChannel(@Validated(Add.class) @RequestBody Channel channel) throws Exception {
        if (StringUtils.isBlank(channel.getDataSource())) {
            channel.setDataSource("BI");
        }
        channelService.saveChannel(channel);
        return ResultEntity.success(true);
    }

    /**
     * 修改渠道
     */
    @PostMapping("/update")
    @ApiOperation(value = "修改渠道", response = ResultEntity.class)
    public ResultEntity<Boolean> updateChannel(@Validated(Update.class) @RequestBody Channel channel) throws Exception {
        boolean flag = channelService.updateChannel(channel);
        return ResultEntity.success(flag);
    }

    /**
     * 删除渠道
     */
    @PostMapping("/delete/{id}")
    @ApiOperation(value = "删除渠道", response = ResultEntity.class)
    public ResultEntity<Boolean> deleteChannel(@PathVariable("id") Long id) throws Exception {
        boolean flag = channelService.deleteChannel(id);
        return ResultEntity.success(flag);
    }

    /**
     * 获取渠道详情
     */
    @GetMapping("/info/{id}")
    @ApiOperation(value = "渠道详情", response = Channel.class)
    public ResultEntity<Channel> getChannel(@PathVariable("id") Long id) throws Exception {
        Channel channel = channelService.getById(id);

        Cooperation cooperation = cooperationService.getOne(new QueryWrapper<Cooperation>().lambda().eq(Cooperation::getId, channel.getCompanyId()));
        channel.setCompanyName(cooperation.getCompanyName());
        channel.setCompanyType(cooperation.getCooperationType() == 1 ? "客户" : "供应商");

        channel.setChannelTypeStr(ChannelTypeEnum.getByKey(Integer.valueOf(channel.getChannelType())).getValue());
        channel.setSecretTypeStr(SecretTypeEnum.getByKey(channel.getSecretType()).getValue());
        channel.setSettlementTypeStr(SettlementTypeEnum.getByKey(Integer.valueOf(channel.getSettlementType())).getValue());

        return ResultEntity.success(channel);
    }

    @PostMapping("/getListByPP")
    @ApiOperation(value = "渠道详情-子渠道信息", response = ChannelChild.class)
    @ApiOperationSupport(includeParameters = {"channelChildPageParam.channelId"})
    public ResultEntity<List<ChannelChild>> getListByPP(@Validated @RequestBody ChannelChildPageParam channelChildPageParam) throws Exception {
        List<ChannelChild> list = channelChildService.getChannelChildListByPP(channelChildPageParam);
        return ResultEntity.success(list);
    }

    /**
     * 渠道分页列表
     */
    @PostMapping("/getPageList")
    @ApiOperation(value = "渠道分页列表")
    public ResultEntity<PageEntity<Channel>> getChannelPageList(@Validated @RequestBody ChannelPageParam channelPageParam, HttpServletRequest request) throws Exception {
        PageEntity<Channel> paging = channelService.getChannelPageList(channelPageParam, request);

        return ResultEntity.success(paging);
    }

    /**
     * 渠道列表
     */
    @PostMapping("/getList")
    @ApiOperation(value = "渠道列表", response = Channel.class)
    @ApiOperationSupport(ignoreParameters = {"channelPageParam.pageIndex", "channelPageParam.pageSorts", "channelPageParam.pageSize"})
    public ResultEntity<List<Channel>> getChannelList(@Validated @RequestBody ChannelPageParam channelPageParam, HttpServletRequest request) throws Exception {
        List<Channel> list = channelService.getChannelList(channelPageParam, request);
        return ResultEntity.success(list);
    }

    /**
     * 渠道列表
     */
    @PostMapping("/getListNoRequest")
    public ResultEntity<List<Channel>> getListNoRequest(@RequestBody ChannelPageParam channelPageParam) throws Exception {
        List<Channel> list = channelService.getChannelList(channelPageParam);
        return ResultEntity.success(list);
    }

    @ApiOperation(value = "渠道列表CRM", response = Channel.class)
    @PostMapping("/getChannelListCRM")
    public ResultEntity<List<Channel>> getChannelListCRM(@RequestBody ChannelPageParam channelPageParam) throws Exception {
        List<Channel> list = channelService.getChannelListCRM(channelPageParam);
        return ResultEntity.success(list);
    }

    @GetMapping("/plug")
    @ApiOperation(value = "插件列表", response = Channel.class)
    public ResultEntity plug() throws Exception {
        String APP_ID = "APP_ID_default";
        String APP_SECRET = "APP_SECRET_QUAfOcACNUGsTRQl";

        String url = "https://dtinfo.hql008.cn/api/sign/plugins";
        Long timestamp = System.currentTimeMillis();
        String str = "appId=" + APP_ID + "&secret=" + APP_SECRET + "&timestamp=" + timestamp;
        String sign = SecureUtil.md5(str).toLowerCase();

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("appId", APP_ID);
        param.put("timestamp", timestamp);
        param.put("sign", sign);

        String result = HttpUtil.createGet(url).form(param).execute().body();

        Set<Map<String, String>> plugList = new HashSet<Map<String, String>>();

        JSONObject resultObject = JSONObject.parseObject(result);
        String code = resultObject.getString("code");

        if ("200".equals(code)) {
            JSONArray jsonArray = resultObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                String pluginName = jsonObject.getString("pluginName");

                Map<String, String> plugMap = new HashMap<String, String>();
                plugMap.put("id", id);
                plugMap.put("name", pluginName);
                plugList.add(plugMap);
            }
        } else {
            return ResultEntity.success("获取失败");
        }

        return ResultEntity.success(plugList);
    }

}

