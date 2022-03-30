package com.stnts.bi.gateway.conf;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties("bi-gateway")
public class GatewayConfiguration {

	private String loginUrl;
	/**
	 * 不需要登录认证URL
	 */
	private List<String> unLoginUrls;
	private String ehomeUrl;
	private String sessionSignal;
	private String biTokenKey;
	private boolean checkLogin;
	private boolean checkAdmin;
	/** 是否包含 */
	private List<String> checkAdminUrls;
	/** 是否匹配 */
	private List<String> unCheckAdminUrls;
	private String biSessionUserKey;
	private String biSessionKeyPre;

	/** Referer配置 */
	private boolean checkReferer;
	private String biReferer;

//	private boolean showStack = true;
}
