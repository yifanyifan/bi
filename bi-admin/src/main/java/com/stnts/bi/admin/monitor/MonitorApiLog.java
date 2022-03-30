package com.stnts.bi.admin.monitor;

import com.alibaba.nacos.client.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.JacksonUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 刘天元
 * @Date: 2021/6/17 11:38
 */
@Component
@Profile({"prod"})
@Slf4j
public class MonitorApiLog {

    final static String url = "https://dtwx-dev.shengtian.com/msg/web?msg=";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    final static String requestKey = "bi:monitor:request";

    final static String responseKey = "bi:monitor:response";

    @KafkaListener(topics = "dt_monitor_api_log")
    public void listen(String message) throws UnsupportedEncodingException {
        String[] split = message.split(",");
        String completion = split[8];
        String requestId = split[4];
        String isSuccess = split[9];
        String business = split[3];
        String moduleName = arrayGet(split, 20);
        String className = arrayGet(split, 21);
        String methodName = arrayGet(split, 22);
        String requestUrl = split[12];
        String startTime = split[5];
        if("0".equals(completion)) {
            Map<String, String> requestInfoMap = new HashMap<>(10);
            requestInfoMap.put("startTime", startTime);
            requestInfoMap.put("business", business);
            requestInfoMap.put("moduleName", moduleName);
            requestInfoMap.put("className", className);
            requestInfoMap.put("methodName", methodName);
            requestInfoMap.put("requestId", requestId);
            requestInfoMap.put("requestUrl", requestUrl);
            String requestInfoStr = JacksonUtil.toJSON(requestInfoMap);
            redisTemplate.opsForHash().put(requestKey, requestId, requestInfoStr);
        } else if("1".equals(completion)) {
            redisTemplate.opsForHash().put(responseKey, requestId, startTime);
        }

        if("0".equals(completion)) {
            // 只处理服务端完成请求的消息
            return;
        }

        if("0".equals(isSuccess)) {
            log.info("接口失败。{}", message);
            StringBuffer stringBuffer = new StringBuffer();
            String exception = arrayGet(split, 23);
            if(!StringUtils.isEmpty(exception) && exception.contains("Broken pipe")) {
                return;
            }
            stringBuffer
                    .append("接口失败。\n")
                    .append("时间:").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.parseLong(startTime))).append(",\n")
                    .append("业务:").append(business).append(",\n")
                    .append("模块名:").append(moduleName).append(",\n")
                    .append("类名:").append(className).append(",\n")
                    .append("方法名:").append(methodName).append(",\n")
                    .append("请求id:").append(requestId).append(",\n")
                    .append("请求url:").append(requestUrl).append(",\n")
                    .append("错误信息:").append(exception);
            doGet(url+ URLEncoder.encode(stringBuffer.toString(), "UTF-8"));
        } else if("1".equals(isSuccess)) {
            String timeStr = split[7];
            int time = Integer.parseInt(timeStr);
            if(time > 15000) {
                log.info("执行时间超过15s。{}", message);
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer
                        .append("接口太慢。\n")
                        .append("时间:").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.parseLong(startTime))).append(",\n")
                        .append("业务:").append(business).append(",\n")
                        .append("模块名:").append(moduleName).append(",\n")
                        .append("类名:").append(className).append(",\n")
                        .append("方法名:").append(methodName).append(",\n")
                        .append("请求id:").append(requestId).append(",\n")
                        .append("请求url:").append(requestUrl).append(",\n")
                        .append("响应时间:").append(time/1000).append("秒");
                doGet(url+ URLEncoder.encode(stringBuffer.toString(), "UTF-8"));
            }
        }
    }

    public static String doGet(String httpUrl) {
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        String result = null;
        try {
            // 创建远程url连接对象
            URL url = new URL(httpUrl);
            // 通过远程url连接对象打开一个连接，强转成httpURLConnection类
            connection = (HttpURLConnection) url.openConnection();
            // 设置连接方式：get
            connection.setRequestMethod("GET");
            // 设置连接主机服务器的超时时间：15000毫秒
            connection.setConnectTimeout(15000);
            // 设置读取远程返回的数据时间：60000毫秒
            connection.setReadTimeout(60000);
            // 发送请求
            connection.connect();
            // 通过connection连接，获取输入流
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                // 封装输入流is，并指定字符集
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                // 存放数据
                StringBuffer sbf = new StringBuffer();
                String temp = null;
                while ((temp = br.readLine()) != null) {
                    sbf.append(temp);
                    sbf.append("\r\n");
                }
                result = sbf.toString();
            }
        } catch (Exception e) {
            log.error("http get error" ,e);
        } finally {
            // 关闭资源
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(null != connection){
                connection.disconnect();// 关闭远程连接
            }
        }
        return result;
    }

    public static String arrayGet(String[] array, int index) {
        if (null == array) {
            return null;
        }

        try {
            return array[index];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public static void main(String[] args) {
        doGet("https://dtwx-dev.shengtian.com/msg/web?msg=%E6%8E%A5%E5%8F%A3%E5%A4%B1%E8%B4%A5%E3%80%82%E4%B8%9A%E5%8A%A1%3AFUSION_BI%2C%E8%AF%B7%E6%B1%82id%3A1405447006276071425%2C%E8%AF%B7%E6%B1%82url%3Ahttp%3A%2F%2F10.244.16.106%2Fsdk%2Fanalyse%2Fchart%2Fget%2C%E9%94%99%E8%AF%AF%E4%BF%A1%E6%81%AF%3AConnectException%3A+Connection+timed+out+%28Connection+timed+out%29");
    }
}
