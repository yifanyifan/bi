package com.stnts.bi.datamanagement.module.cooperation.controller;


import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.constant.CooperationConstant;
import com.stnts.bi.datamanagement.constant.DataSourceConstant;
import com.stnts.bi.datamanagement.constant.EnvironmentProperties;
import com.stnts.bi.datamanagement.module.cooperation.entity.CooperationBi;
import com.stnts.bi.datamanagement.module.cooperation.param.CooperationAddApiParam;
import com.stnts.bi.datamanagement.util.JacksonUtil;
import com.stnts.bi.datamanagement.util.JwtUtil;
import com.stnts.bi.datamanagement.util.SignUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * <p>
 * 合作伙伴汇总表 前端控制器
 * </p>
 *
 * @author liutianyuan
 * @since 2020-07-03
 */
@RestController
@RequestMapping("/api/cooperation")
public class CooperationApiController {
    private static final Logger logger = LoggerFactory.getLogger(CooperationApiController.class);

    private final CooperationController cooperationController;

    private final CooperationBiController cooperationBiController;

    private final CooperationCrmController cooperationCrmController;

    private final EnvironmentProperties environmentProperties;

    private final SignUtil signUtil;

    private final JwtUtil jwtUtil;

    public CooperationApiController(CooperationController cooperationController, CooperationBiController cooperationBiController, CooperationCrmController cooperationCrmController, EnvironmentProperties environmentProperties, SignUtil signUtil, JwtUtil jwtUtil) {
        this.cooperationController = cooperationController;
        this.cooperationBiController = cooperationBiController;
        this.cooperationCrmController = cooperationCrmController;
        this.environmentProperties = environmentProperties;
        this.signUtil = signUtil;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/save")
    public ResultEntity save(String cooperation, String appId, Long timestamp, String sign) {
        signUtil.checkSign(appId, timestamp, sign, Dict.create().set("cooperation", cooperation));
        CooperationBi cooperationBi = JacksonUtil.fromJSON(cooperation, CooperationBi.class);
        cooperationBi.setDataSource(DataSourceConstant.dataSourceOrderSystem);
        cooperationBi.setLastStatus(CooperationConstant.LAST_STATUS_ON);
        cooperationBi.setLastRemark("订单系统后台新增");
        return cooperationBiController.save(cooperationBi, null);
    }

    @PostMapping("/update")
    public ResultEntity update(String cooperation, String appId, Long timestamp, String sign) {
        signUtil.checkSign(appId, timestamp, sign, Dict.create().set("cooperation", cooperation));
        // 校验数据来源
        return cooperationBiController.update(JacksonUtil.fromJSON(cooperation, CooperationBi.class), null);
    }

    @GetMapping("/get")
    public ResultEntity get(Long id, String appId, Long timestamp, String sign) {
        signUtil.checkSign(appId, timestamp, sign, Dict.create().set("id", id));
        return cooperationController.get(id, null);
    }

    @GetMapping("/list")
    public ResultEntity list(Integer currentPage, Integer pageSize, String companyName, String cpName, String parentIndustry, String childIndustry, String keyword, Integer cooperationType,
                             String appId, Long timestamp, String sign, HttpServletRequest request) {
        Dict dict = Dict.create().set("currentPage", currentPage).set("pageSize", pageSize).set("companyName", companyName).set("cpName", cpName).set("parentIndustry", parentIndustry).set("childIndustry", childIndustry)
                .set("keyword", keyword).set("cooperationType", cooperationType);
        signUtil.checkSign(appId, timestamp, sign, dict);
        return cooperationController.list(currentPage, pageSize, companyName, cpName, parentIndustry, childIndustry, keyword, cooperationType, request);
    }

    @GetMapping("/listall")
    public ResultEntity listAll(String companyName, String cpName, String parentIndustry, String childIndustry, Integer cooperationType, String appId, Long timestamp, String sign) {
        logger.info("===========================>公司列表 请求参数：" + "companyName:" + companyName + ",cpName:" + cpName + ",parentIndustry:" + parentIndustry + ",childIndustry:" + childIndustry + ",cooperationType:" + cooperationType + ",appId:" + appId + ",timestamp:" + timestamp + ",sign:" + sign);

        signUtil.checkSign(appId, timestamp, sign, Dict.create().set("companyName", companyName).set("cpName", cpName).set("parentIndustry", parentIndustry).set("childIndustry", childIndustry).set("cooperationType", cooperationType));

        return cooperationController.listAll(companyName, cpName, parentIndustry, childIndustry, cooperationType);
    }

    @PostMapping("/addCompany")
    public ResultEntity<Map<String, String>> addCompany(@RequestBody CooperationAddApiParam cooperationAddApiParam) {
        logger.info("=================>CRM保存，请求参数" + JSON.toJSONString(cooperationAddApiParam));

        String appId = cooperationAddApiParam.getAppId();
        Long timestamp = cooperationAddApiParam.getTimestamp();
        String sign = cooperationAddApiParam.getSign();

        signUtil.checkSign(appId, timestamp, sign, Dict.create()
                .set("customerId", cooperationAddApiParam.getCustomerId())
                .set("customerName", cooperationAddApiParam.getCustomerName())
                .set("companyId", cooperationAddApiParam.getCompanyId())
                .set("companyName", cooperationAddApiParam.getCompanyName())
                .set("cooperationType", cooperationAddApiParam.getCooperationType())
                .set("companyTaxkey", cooperationAddApiParam.getCompanyTaxkey())
                .set("isProtection", cooperationAddApiParam.getIsProtection())
                .set("isTest", cooperationAddApiParam.getIsTest())
                .set("inSystem", cooperationAddApiParam.getInSystem())
                .set("natureContract", cooperationAddApiParam.getNatureContract())
                .set("companyLegal", cooperationAddApiParam.getCompanyLegal())
                .set("companyQualification", cooperationAddApiParam.getCompanyQualification())
                .set("bankName", cooperationAddApiParam.getBankName())
                .set("bankNumber", cooperationAddApiParam.getBankNumber())
                .set("cardNumber", cooperationAddApiParam.getCardNumber())
                .set("crmType", cooperationAddApiParam.getCrmType())
                .set("channelId", cooperationAddApiParam.getChannelId())
                .set("channelName", cooperationAddApiParam.getChannelName())
                .set("departmentCode", cooperationAddApiParam.getDepartmentCode())
                .set("departmentName", cooperationAddApiParam.getDepartmentName())
                .set("channelType", cooperationAddApiParam.getChannelType())
                .set("secretType", cooperationAddApiParam.getSecretType())
                .set("settlementType", cooperationAddApiParam.getSettlementType())
                .set("userId", cooperationAddApiParam.getUserId())

        );
        return cooperationCrmController.addCompany(cooperationAddApiParam);
    }

    @PostMapping("/updateCompany")
    public ResultEntity<Map<String, String>> updateCompany(@RequestBody CooperationAddApiParam cooperationAddApiParam) {
        logger.info("=================>CRM更新公司，请求参数" + JSON.toJSONString(cooperationAddApiParam));

        String appId = cooperationAddApiParam.getAppId();
        Long timestamp = cooperationAddApiParam.getTimestamp();
        String sign = cooperationAddApiParam.getSign();

        signUtil.checkSign(appId, timestamp, sign, Dict.create()
                .set("companyId", cooperationAddApiParam.getCompanyId())
                .set("companyName", cooperationAddApiParam.getCompanyName())
                .set("cooperationType", cooperationAddApiParam.getCooperationType())
                .set("companyTaxkey", cooperationAddApiParam.getCompanyTaxkey())
                .set("isProtection", cooperationAddApiParam.getIsProtection())
                .set("isTest", cooperationAddApiParam.getIsTest())
                .set("inSystem", cooperationAddApiParam.getInSystem())
                .set("natureContract", cooperationAddApiParam.getNatureContract())
                .set("bankName", cooperationAddApiParam.getBankName())
                .set("bankNumber", cooperationAddApiParam.getBankNumber())
                .set("cardNumber", cooperationAddApiParam.getCardNumber())
        );
        return cooperationCrmController.updateCompany(cooperationAddApiParam);
    }

    @PostMapping("/updateChannel")
    public ResultEntity<Map<String, String>> updateChannel(@RequestBody CooperationAddApiParam cooperationAddApiParam) {
        logger.info("=================>CRM更新渠道，请求参数" + JSON.toJSONString(cooperationAddApiParam));

        String appId = cooperationAddApiParam.getAppId();
        Long timestamp = cooperationAddApiParam.getTimestamp();
        String sign = cooperationAddApiParam.getSign();

        signUtil.checkSign(appId, timestamp, sign, Dict.create()
                .set("companyId", cooperationAddApiParam.getCompanyId())
                .set("channelId", cooperationAddApiParam.getChannelId())
        );
        return cooperationCrmController.updateChannel(cooperationAddApiParam);
    }

    @GetMapping("/url")
    public ResultEntity url(String userId, String userName, String appId, Long timestamp, String sign) {
        signUtil.checkSign(appId, timestamp, sign, Dict.create().set("userId", userId).set("userName", userName));
        String departmentCode = getDepartmentCode(userId);
        String departmentName = getDepartmentName(departmentCode);
        Map<String, String> userInfoJWT = new HashMap<>(5);
        userInfoJWT.put("id", userId);
        userInfoJWT.put("name", userName);
        userInfoJWT.put("department", departmentName);
        userInfoJWT.put("departmentCode", departmentCode);
        String token = jwtUtil.get(userInfoJWT);
        return ResultEntity.success(Dict.create().set("url", environmentProperties.getAddCooperatorUrl()).set("token", token));
    }

    /**
     * 获取一级部门code
     *
     * @param userId
     * @return
     */
    private String getDepartmentCode(String userId) {
        Map userInfo = (Map) getUamsUserInfo(userId);
        String departmentCode = MapUtil.getStr(userInfo, "code");
        String subDepartmentCode = StrUtil.sub(departmentCode, 0, 5);
        return subDepartmentCode;
    }

    /**
     * 获取部门名称
     *
     * @param departmentCode
     * @return
     */
    private String getDepartmentName(String departmentCode) {
        String departmentName = "";
        List departmentList = (List) departmentList();
        for (Object departmentObj : departmentList) {
            Object code = ((Map) departmentObj).get("code");
            if (departmentCode.equals(code)) {
                departmentName = ((Map) departmentObj).get("name").toString();
            }
        }
        return departmentName;
    }

    private Object getUamsUserInfo(String userid) {
        Long timestamp = System.currentTimeMillis();
        TreeMap<String, Object> params = MapUtil.newTreeMap(Comparator.comparing(String::toString));
        params.put("userid", userid);
        params.put("appId", environmentProperties.getUamsAppId());
        params.put("timestamp", timestamp);
        String sign = signUtil.getSign(params, environmentProperties.getUamsSecret());
        params.put("format", "json");
        params.put("sign", sign);
        return getUamsData(StrUtil.format("http://{}/ump/api/getUserInfo4NewDcode", environmentProperties.getUamsAddress()), params);
    }

    private Object departmentList() {
        Long timestamp = System.currentTimeMillis();
        TreeMap<String, Object> params = MapUtil.newTreeMap(Comparator.comparing(String::toString));
        params.put("appId", environmentProperties.getUamsAppId());
        params.put("timestamp", timestamp);
        String sign = signUtil.getSign(params, environmentProperties.getUamsSecret());
        params.put("format", "json");
        params.put("sign", sign);
        return getUamsData(StrUtil.format("http://{}/ump/api/listDepartment4NewDcode", environmentProperties.getUamsAddress()), params);
    }

    private Object getUamsData(String url, TreeMap<String, Object> params) {
        String result = HttpUtil.get(url, params);
        Map map = JacksonUtil.fromJSON(result, Map.class);
        return map.get("data");
    }

    @PostMapping("/bi/upload")
    public ResultEntity upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Map<String, String> userInfo = jwtUtil.verify(token);
        return cooperationBiController.upload(file);
    }

    @PostMapping("/create")
    public ResultEntity save(@RequestBody CooperationBi cooperationBi, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Map<String, String> userInfo = jwtUtil.verify(token);
        cooperationBi.setCreateUser(userInfo.get("name"));
        cooperationBi.setCreateUserId(userInfo.get("id"));
        cooperationBi.setCreateDepartmentCode(userInfo.get("departmentCode"));
        cooperationBi.setCreateDepartment(userInfo.get("department"));
        cooperationBi.setDataSource(DataSourceConstant.dataSourceOrderSystem);
        cooperationBi.setLastStatus(CooperationConstant.LAST_STATUS_ON);
        cooperationBi.setLastRemark("订单系统后台新增");
        return cooperationBiController.save(cooperationBi, null);
    }

}
