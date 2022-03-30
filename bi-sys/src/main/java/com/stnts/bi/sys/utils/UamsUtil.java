package com.stnts.bi.sys.utils;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;

/**
 * @author liang.zhang
 * @date 2020年7月22日
 * @desc TODO
 *   这里放些uams请求接口
 */
@Slf4j
public class UamsUtil {

	public static String initSign(String appId, String key, long timestamp){
		// appId=USERCENTER&timestamp=ms 密钥
		String signStr = String.format("appId=%s&timestamp=%s%s", appId, timestamp, key);
		return  SecureUtil.md5(signStr).toLowerCase();
	}

	public static JSONArray listUserInfo(String appId, String key, long timestamp, String getUrlTemplate) throws Exception {

		JSONArray arr = new JSONArray();
		String initSign = UamsUtil.initSign(appId, key, timestamp);
		String getUrl = String.format(getUrlTemplate, appId, timestamp, initSign);
		String callback = HttpUtil.get(getUrl);
		if(StringUtils.isNotBlank(callback)){
			JSONObject obj = JSON.parseObject(callback);
			arr = obj.getJSONArray("data");
		}
		return arr;
	}

	public static void main(String[] args) {
		long ts = Instant.now().toEpochMilli();
		try {
			listUserInfo("BI", "EqSfj1n*$Rf31V", ts, "http://api.ehome-dev.stnts.com/ump/api/listOrgsTree?appId=%s&timestamp=%s&sign=%s");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
