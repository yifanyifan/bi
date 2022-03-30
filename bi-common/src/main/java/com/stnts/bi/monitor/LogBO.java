package com.stnts.bi.monitor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.servlet.http.HttpServletRequest;

/**
 * @author liutianyuan
 * @date 2021-06-16 15:19
 */

@Data
public class LogBO {

    @JsonIgnore
    HttpServletRequest request;

    /**
     * 业务，如FUSION_BI,OLAP
     */
    private String business;
    /**
     * 每个请求分配的唯一id
     */
    @JsonProperty("request_id")
    private Long requestId;
    /**
     * 开始时间
     */
    @JsonProperty("start_time")
    private Long startTime;
    /**
     * 结束时间
     */
    @JsonProperty("end_time")
    private Long endTime;
    /**
     * 响应时间
     */
    @JsonProperty("dotime")
    private Integer time;

    /**
     * 是否完成。0:服务端收到请求；1：服务端完成请求处理。
     */
    private Integer completion;
    /**
     * 是否成功。0:失败；1：成功。
     */
    @JsonProperty("is_success")
    private Integer success;
    /**
     * 用户标识
     */
    @JsonProperty("userid")
    private Integer userId;
    /**
     * 用户名
     */
    @JsonProperty("username")
    private String userName;
    /**
     * 请求url
     */
    @JsonProperty("request_url")
    private String requestUrl;
    /**
     * http method。GET、POST等等
     */
    private String method;
    /**
     * 请求参数。url中的参数。
     */
    @JsonProperty("request_parameter")
    private String requestParameter;
    /**
     * http request body.
     */
    @JsonProperty("request_body")
    private String requestBody;
    /**
     * cookie
     */
    private String cookie;
    /**
     * IP地址
     */
    @JsonProperty("ip_address")
    private String ipAddress;
    /**
     * http referer
     */
    private String referer;
    /**
     * 请求结果
     */
    private String result;
    /**
     * 模块名
     */
    @JsonProperty("module_name")
    private String moduleName;
    /**
     * 类名
     */
    @JsonProperty("class_name")
    private String className;
    /**
     * 方法名
     */
    @JsonProperty("method_name")
    private String methodName;
    /**
     * 错误信息或说明
     */
    private String message;
}