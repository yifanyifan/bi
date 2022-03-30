//package com.stnts.bi.gateway.conf;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.session.SaveMode;
//import org.springframework.session.data.redis.config.annotation.web.server.RedisWebSessionConfiguration;

//@Configuration
//public class SpringSessionConfig extends RedisWebSessionConfiguration{
//	
//	@Autowired
//	private GatewayConfiguration gateWayConfiguration;
//	
//
//	public SpringSessionConfig() {
//		
//        super();
//        System.out.println(gateWayConfiguration.getEhomeUrl());
//        super.setRedisNamespace("bi");
//        super.setMaxInactiveIntervalInSeconds(60);
//        super.setSaveMode(SaveMode.ALWAYS);
//    }
//}
