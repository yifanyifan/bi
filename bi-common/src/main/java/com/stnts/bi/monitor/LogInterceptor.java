package com.stnts.bi.monitor;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.stnts.bi.common.BiSessionUtil;
import com.stnts.bi.entity.sys.UserEntity;
import com.stnts.bi.utils.IpAdrressUtil;
import com.stnts.bi.utils.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

/**
 * @author liutianyuan
 * @date 2021-06-16 15:19
 */

@Component
@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    public final static String REQUEST_ID = "requestId";

    private final static String STEP_PRE = "pre";

    private final static String STEP_AFTER = "after";

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Environment environment;

    private final static String TEST_PROFILE = "test";
    private final static String PROD_PROFILE = "prod";

    private final static String DRM_TEST = "http://10.0.45.141:9999/?opt=put&type=json";
    private final static String DRM_PROD = "http://olapdb01:8888/?opt=put&type=json";

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String referer = request.getHeader("Referer");

        String ipAddress = IpAdrressUtil.getIpAddress(request);
        String method = request.getMethod();
        StringBuffer requestURL = request.getRequestURL();
        Map<String, String[]> parameterMap = request.getParameterMap();
        Cookie[] cookies = request.getCookies();
        Map<String, String> cookieMap = cookies == null || cookies.length == 0 ? null :
                stream(cookies).collect(Collectors.toMap(Cookie::getName, Cookie::getValue, (oldValue, newValue) -> String.join(",", oldValue, newValue)));

        Long requestId = IdWorker.getId();
        LogBO logBO = new LogBO();
        logBO.setRequest(request);
        logBO.setBusiness("FUSION_BI");
        logBO.setRequestId(requestId);
        logBO.setStartTime(System.currentTimeMillis());
        logBO.setRequestUrl(requestURL.toString());
        logBO.setMethod(method);
        logBO.setCookie(JacksonUtil.toJSON(cookieMap));
        logBO.setRequestParameter(getParameter(parameterMap));
        logBO.setIpAddress(ipAddress);
        logBO.setReferer(referer);

        logBO.setModuleName(environment.getProperty("spring.application.name"));
        if(handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            logBO.setClassName(handlerMethod.getBeanType().getName());
            logBO.setMethodName(handlerMethod.getMethod().getName());
        }

        log.info("{} start", requestId);
        MDC.put(REQUEST_ID, requestId.toString());
        ThreadLocalLog.set(logBO);
        writeLogAsync(logBO, STEP_PRE);


        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        LogBO logBO = ThreadLocalLog.get();
        logBO.setEndTime(System.currentTimeMillis());
        Long time = logBO.getEndTime() - logBO.getStartTime();
        logBO.setTime(time.intValue());

        log.info("{} end {}.{} ", logBO.getRequestId(), logBO.getClassName(), logBO.getMethodName());
        writeLogAsync(logBO, STEP_AFTER);
        ThreadLocalLog.remove();
        MDC.remove(REQUEST_ID);
    }

    private void writeLogAsync(LogBO logBO, String step) {
        BiSessionUtil sessionUtil = getSessionUtil(logBO);
        executorService.execute(() -> {
            MDC.put(REQUEST_ID, logBO.getRequestId() + "异步写日志" + step);

            try {
                if(sessionUtil != null) {
                    UserEntity user = sessionUtil.getSessionUser();
                    if(user != null) {
                        logBO.setUserId(user.getId());
                        logBO.setUserName(user.getCnname());
                        log.info("用户id:{},用户名:{}", logBO.getUserId(), logBO.getUserName());
                    }
                }
            } catch (Exception e) {
                log.error("get user error", e);
            }

            if(STEP_PRE.equals(step)) {
                logBO.setCompletion(0);
            } else if(STEP_AFTER.equals(step)) {
                logBO.setCompletion(1);
            }

            Set<String> activeProfileSet = Arrays.stream(environment.getActiveProfiles()).collect(Collectors.toSet());
            String url = "";
            if(activeProfileSet.contains(TEST_PROFILE)) {
                url = DRM_TEST;
            } else if(activeProfileSet.contains(PROD_PROFILE)) {
                url = DRM_PROD;
            }

            if(!StringUtils.isEmpty(url)) {
                HttpHeaders headers = new HttpHeaders();
                MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
                headers.setContentType(type);
                String requestJson = JacksonUtil.toJSON(new SdkJsonTemplate(logBO));
                HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
                String result = restTemplate.postForObject(url, entity, String.class);
            }

            MDC.remove(REQUEST_ID);
        });
    }

    private BiSessionUtil getSessionUtil(LogBO logBO) {
        try {
            return BiSessionUtil.build(redisTemplate, logBO.getRequest());
        } catch (Exception e) {
            log.info("无法获取session");
            return null;
        }
    }


    private String getParameter(Map<String, String[]> requestParameter) {
        StringBuffer stringBuffer = new StringBuffer();
        requestParameter.forEach((key, value) -> {
            if(value==null || value.length==0) {
                stringBuffer.append(key).append("=").append("&");
            } else {
                for (String str : value) {
                    stringBuffer.append(key).append("=").append(str).append("&");
                }
            }
        });
        return stringBuffer.toString();
    }

}
