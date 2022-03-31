package com.stnts.bi.admin.monitor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author liutianyuan
 * @date 2019-09-25 14:47
 */


@Component
@Slf4j
public class CheckRequestCompleteSchedule {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 每分钟执行一次
     */
    //@Scheduled(fixedDelay = 60000)
    public void checkRequestCompleteSchedule() {
        log.info("checkRequestCompleteSchedule start");
        Map<Object, Object> requestEntries = redisTemplate.opsForHash().entries(MonitorApiLog.requestKey);
        Map<Object, Object> responseEntries = redisTemplate.opsForHash().entries(MonitorApiLog.responseKey);

        long currentTimeMillis = System.currentTimeMillis();

        requestEntries.forEach((requestKey, requestValue) -> {
            Object responseValue = responseEntries.get(requestKey);
            if(responseValue != null) {
                log.info("请求{}已完成",requestKey);
                redisTemplate.opsForHash().delete(MonitorApiLog.requestKey, requestKey);
                redisTemplate.opsForHash().delete(MonitorApiLog.responseKey, requestKey);
            } else {
                Map<String, String> requestInfoMap = JacksonUtil.fromJSON(requestValue.toString(), Map.class);

                long startTime = Long.parseLong(requestInfoMap.get("startTime"));
                if((currentTimeMillis - startTime) > 60000) {
                    log.info("请求{}已执行超过一分钟，并且没有返回结果。", requestKey);
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer
                            .append("接口阻塞。\n")
                            .append("时间:").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startTime)).append(",\n")
                            .append("业务:").append(requestInfoMap.get("business")).append(",\n")
                            .append("模块名:").append(requestInfoMap.get("moduleName")).append(",\n")
                            .append("类名:").append(requestInfoMap.get("className")).append(",\n")
                            .append("方法名:").append(requestInfoMap.get("methodName")).append(",\n")
                            .append("请求id:").append(requestInfoMap.get("requestId")).append(",\n")
                            .append("请求url:").append(requestInfoMap.get("requestUrl"));
                    try {
                        MonitorApiLog.doGet(MonitorApiLog.url+ URLEncoder.encode(stringBuffer.toString(), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    redisTemplate.opsForHash().delete(MonitorApiLog.requestKey, requestKey);
                }
            }
        });


        // 计算response与request的差集。
        Set<Object> responseRemoveAllRequest = new HashSet<>(responseEntries.keySet());
        responseRemoveAllRequest.removeAll(requestEntries.keySet());

        for (Object responseKey : responseRemoveAllRequest) {
            Object responseValue = responseEntries.get(responseKey);
            long responseTime = Long.parseLong(responseValue.toString());
            if((currentTimeMillis - responseTime) > 60000 * 60 * 60) {
                //当前时间超过响应时间一个小时
                redisTemplate.opsForHash().delete(MonitorApiLog.responseKey, responseKey);
            }
        }
    }

}
