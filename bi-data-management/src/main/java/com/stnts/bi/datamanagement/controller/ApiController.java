package com.stnts.bi.datamanagement.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.datamanagement.service.ApiService;
import com.stnts.bi.datamanagement.vo.ApiSimpleVO;
import com.stnts.bi.datamanagement.vo.ApiVO;
import com.stnts.bi.utils.BigDecimalUtils;
import com.stnts.bi.vo.CjtCCIDVO;
import com.stnts.signature.annotation.SignedMapping;
import com.stnts.signature.entity.SignedParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/5/12
 */
@RestController
@RequestMapping("api")
@Api(value = "数据管理对外接口", tags = {"数据管理对外接口"})
@SignedMapping
@Slf4j
public class ApiController {

    @Autowired
    private ApiService apiService;

//    @PostMapping("initPid/main")
//    @ApiOperation(value = "生成pid主方法", response = ResultEntity.class, notes = "签名算法如下： signature=MD5(appId=APP_ID&nonce=123&timestamp=1612325632077APP_SECRET)")
//    public ResultEntity<Map<Object, Object>> initPid(@RequestBody ApiVO apiVO, SignedParam signedParam){
//
//        Map<Object, Object> objectMap = apiService.initPid(apiVO);
//        return ResultEntity.success(objectMap);
//    }

    @PostMapping("initPid/ylw/advanced")
    @ApiOperation(value = "易乐玩-三合一", response = ResultEntity.class, notes = "签名算法如下： signature=MD5(appId=APP_ID&nonce=123&timestamp=1612325632077APP_SECRET)")
    public ResultEntity<Map<Object, Object>> initPid4YlwAdvanced(@RequestBody ApiVO apiVO, SignedParam signedParam) {
        log.info("===========================>易乐玩-三合一, apiVO={}, signedParam={}", JSON.toJSONString(apiVO), JSON.toJSONString(signedParam));

        Map<Object, Object> map = apiService.initPid4Ylw(apiVO);
        return ResultEntity.success(map);
    }

    @PostMapping("initPid/ylw/simple")
    @ApiOperation(value = "易乐玩-简版", response = ResultEntity.class, notes = "签名算法如下： signature=MD5(appId=APP_ID&nonce=123&timestamp=1612325632077APP_SECRET)")
    public ResultEntity<Map<Object, Object>> initPid4YlwSimple(@RequestBody ApiSimpleVO apiSimpleVO, SignedParam signedParam) {
        log.info("===============================>易乐玩-简版, apiVO={}, signedParam={}", JSON.toJSONString(apiSimpleVO), JSON.toJSONString(signedParam));

        Map<Object, Object> map = apiService.initPid4YlwSimple(apiSimpleVO);
        return ResultEntity.success(map);
    }

    public static void main(String[] args) {
        String s = "{\"ccid\":\"CCID76774CPS0n3\",\"channelId\":\"10164\",\"channelName\":\"顺网_互动娱乐事业部\",\"channelRate\":\"0.0000\",\"channelShareType\":\"1\",\"chargeRule\":\"CPS\",\"pid\":\"H6WSW0X01624\"}";
        CjtCCIDVO cjtCCIDVO = JSON.parseObject(s, CjtCCIDVO.class);
        getBaseByDay(cjtCCIDVO, "639.0000");
        System.out.println("111");
    }
    public static String getBaseByDay(CjtCCIDVO cjtCCIDVO, String regBase) {
        String base = "";

        if (ObjectUtil.isNotEmpty(cjtCCIDVO.getChannelShare())) {
            base = cjtCCIDVO.getChannelShare().toString();
        } else if (ObjectUtil.isNotEmpty(cjtCCIDVO.getChannelShareStep())) {
            // [{"num":["1","2"],"_reactId":0,"share":"3.00"},{"_reactId":"1ob6a7ed3cs","num":["4","5"],"share":"6.00"}]
            JSONArray jsonArray = JSONUtil.parseArray(cjtCCIDVO.getChannelShareStep());
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONArray jsonArraySub = JSONUtil.parseArray(jsonObject.get("num"));
                log.info("jsonArraySub:" + JSON.toJSONString(jsonArraySub));

                if (jsonArraySub.size() != 1) {
                    String start = jsonArraySub.getStr(0);
                    String end = jsonArraySub.getStr(1);
                    if ((i == jsonArray.size() - 1) || (BigDecimalUtils.compare(end, regBase) && BigDecimalUtils.compare(regBase, start))) {
                        base = jsonObject.getStr("share");
                        break;
                    }
                } else {
                    String start = jsonArraySub.getStr(0);
                    if ((i == jsonArray.size() - 1) || (BigDecimalUtils.compare(regBase, start))) {
                        base = jsonObject.getStr("share");
                        break;
                    }
                }

            }
        }

        return base;
    }
}
