package com.stnts.bi.sys.utils;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/1/13
 */
@Slf4j
public class OlapUtil {

    public static final String BODY_TEMPLATE = "appId=%s&dashboardId=%s&timestamp=%s";
    public static final String SIGN_TEMPLATE = "appId=%s&timestamp=%s";

    public static final String KEY_SUCCESS = "success";


    public static Map<Integer, String> loadOlapUrl(List<Integer> dashboardIdList, String appId, String key, String host){

        Map<Integer, String> urlMap = new HashMap<Integer, String>();
        try{

            String join = StringUtils.join(dashboardIdList, "&dashboardId=");
            long timestamp = Instant.now().toEpochMilli();
            String params = String.format(BODY_TEMPLATE, appId, join, timestamp);
            String content = String.format(SIGN_TEMPLATE, appId, timestamp).concat(key);
            String sign = SecureUtil.md5(content).toLowerCase();
            String api = String.format("%s?%s&sign=%s", host, params, sign);
            log.info("api: {}", api);
            String post = HttpUtil.post(api, "", 3000);
            System.out.println(post);
            if(StringUtils.isNotBlank(post)){
                JSONObject resultObj = JSON.parseObject(post);
                String status = resultObj.getString("status");
                if(StringUtils.equals(status, KEY_SUCCESS)){
                    JSONArray arrObj = resultObj.getJSONArray("data");
                    for(int i = 0 ; i < arrObj.size() ; i++){
                        JSONObject jsonObject = arrObj.getJSONObject(i);
                        String url = jsonObject.getString("url");
                        //BI这边分享的URL带上head=0的参数去掉页面头部
                        url = StringUtils.isNotBlank(url) ? url.concat("?head=0") : url;
                        urlMap.put(jsonObject.getIntValue("dashboardId"), url);
                    }
                }
            }
        }catch(Exception e){
            log.warn("请求OLAP获取仪表盘请求路径出错了, 异常信息: {}", e.getMessage());
        }
        return urlMap;
    }

    public static void main(String[] args) {
        loadOlapUrl(Arrays.asList(10455,10283, 0), "bi-test", "PphtJ4zA58XmUsQwXmWF", "http://10.0.44.15:8021/api/bi");
    }
}
