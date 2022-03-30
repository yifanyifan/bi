package com.stnts.bi.datamanagement;


import cn.hutool.core.map.MapUtil;
import cn.hutool.http.HttpUtil;
import com.stnts.bi.datamanagement.constant.EnvironmentProperties;
import com.stnts.bi.datamanagement.util.JacksonUtil;
import com.stnts.bi.datamanagement.util.SignUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    @Autowired
    private EnvironmentProperties environmentProperties;

    @Autowired
    private  SignUtil signUtil;

    @Test
    public void test() {
        Long timestamp = System.currentTimeMillis();
        TreeMap<String, Object> params = MapUtil.newTreeMap(Comparator.comparing(String::toString));
        params.put("userId", 4194);
        params.put("userName", "test");
        params.put("appId", environmentProperties.getAppId());
        params.put("timestamp", timestamp);
        String sign = signUtil.getSign(params, environmentProperties.getAppSecret());

        params.put("sign", sign);


        String result = HttpUtil.get("http://bi.stnts.com/bi-bak/bi-data-management/datamanagement/api/cooperation/url", params);
        //String result = HttpUtil.get("http://localhost:18885/datamanagement/api/cooperation/url", params);
        Map map = JacksonUtil.fromJSON(result, Map.class);
        System.out.println(map);

    }


}
