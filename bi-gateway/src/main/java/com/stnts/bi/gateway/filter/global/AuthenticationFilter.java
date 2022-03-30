package com.stnts.bi.gateway.filter.global;

import java.net.URI;
import java.net.URL;
import java.util.List;

import cn.hutool.core.util.URLUtil;
import com.stnts.bi.gateway.utils.RespUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.alibaba.fastjson.JSON;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.gateway.conf.GatewayConfiguration;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * @author liang.zhang
 */
@Component
@Slf4j
public class AuthenticationFilter implements GlobalFilter, Ordered {

	public static final String BI_SESSION_SIGNAL = "1";
	public static final String BI_KEY_ADMIN = "admin";

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private GatewayConfiguration conf;

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		String tokenKey = conf.getBiTokenKey();
		String sessionSignal = conf.getSessionSignal();
		ServerHttpRequest request = exchange.getRequest();
		String reqUrl = request.getPath().value();
//		String reqPath = URLUtil.getPath(reqUrl);
//		String loginUrl = conf.getLoginUrl();
//		System.out.println("reqUrl: " + reqUrl + ", loginUrl: " + loginUrl);
		return exchange.getSession().flatMap(webSession -> {
			//触发session持久化..always没效果
			webSession.getAttributes().put(sessionSignal, BI_SESSION_SIGNAL);
			String sessionId = webSession.getId();
//			log.info("sessionId: {}", sessionId);
			//是否进行referer验证 防止csrf
			if(conf.isCheckReferer() && !containsUrl(conf.getUnLoginUrls(), reqUrl)){
				String referer = request.getHeaders().getOrEmpty("Referer").stream().findFirst().orElse("");
				if(StringUtils.isBlank(referer) || !StringUtils.equals(URLUtil.getHost(URLUtil.toUrlForHttp(referer)).getHost(), conf.getBiReferer())){
					return returnInvalidReferer(exchange);
				}
//					log.info("reqUrl: {}, Referer: {}, refHost: {}", reqUrl, referer, refHost);
			}

			//非前往登录页面 且 开启登录认证
			if(!containsUrl(conf.getUnLoginUrls(), reqUrl) && conf.isCheckLogin()) {
				String hashKey = conf.getBiSessionKeyPre().concat(sessionId);
				Object user = null;
				int i = 0;
				while(user == null && ++i <= 3){
					try{
						user = redisTemplate.opsForHash().get(hashKey, conf.getBiSessionUserKey());
					}catch(Exception e){
						log.warn("获取用户信息{}次后仍然失败,异常信息: {}", i, e.getMessage());
					}
				}
				if(null == user) {
					return returnUnLogin(exchange);
				}
				//这里对于bi-sys需要做控制，必须是超级管理员才能进
				if(conf.isCheckAdmin() && containsUrl(conf.getCheckAdminUrls(), reqUrl) && !conf.getUnCheckAdminUrls().contains(reqUrl)) {
					if(JSON.parseObject(String.valueOf(user)).getIntValue(BI_KEY_ADMIN) != 1) {
						return returnNotAdmin(exchange);
					}
				}
			}
			ServerHttpRequest host = exchange.getRequest().mutate().header(tokenKey, new String[] { sessionId })
					.build();
			ServerWebExchange build = exchange.mutate().request(host).build();
			return chain.filter(build);
		}).then(Mono.fromRunnable(() -> log.info("Request url {} add bi token", reqUrl)));
	}

	/**
	 * @param urls
	 * @param req
	 * @return
	 */
	private boolean containsUrl(List<String> urls, String req) {

		for(String url : urls) {
			if(StringUtils.startsWith(req, url)){
				return true;
			}
		}
		return false;
	}

	private Mono<Void> returnUnLogin(ServerWebExchange exchange) {
		String resultStr = JSON.toJSONString(ResultEntity.custom(40003, "请重新登录", "未登录或Session已过期"));
		return RespUtil.returnMessage(exchange, resultStr);
	}

	private Mono<Void> returnInvalidReferer(ServerWebExchange exchange) {
		String resultStr = JSON.toJSONString(ResultEntity.custom(40003, "非法请求", "非法请求"));
		return RespUtil.returnMessage(exchange, resultStr);
	}

	private Mono<Void> returnNotAdmin(ServerWebExchange exchange) {
		String resultStr = JSON.toJSONString(ResultEntity.custom(40003, "非超级管理员", "非超级管理员不允许访问"));
		return RespUtil.returnMessage(exchange, resultStr);
	}

//	private Mono<Void> returnMessage(ServerWebExchange exchange, String resultStr) {
//        ServerHttpResponse response = exchange.getResponse();
//        HttpStatus httpStatus = HttpStatus.OK;
//        byte[] bits = resultStr.getBytes(StandardCharsets.UTF_8);
//        DataBuffer buffer = response.bufferFactory().wrap(bits);
//        response.setStatusCode(httpStatus == null ? HttpStatus.OK : httpStatus);
//        //指定编码，否则在浏览器中会中文乱码
//        response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
//        return response.writeWith(Mono.just(buffer));
//    }

}
