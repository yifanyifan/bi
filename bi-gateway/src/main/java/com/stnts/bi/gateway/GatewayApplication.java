package com.stnts.bi.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.SaveMode;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;

@SpringBootApplication
@EnableRedisWebSession(maxInactiveIntervalInSeconds = 43200, redisNamespace = "bi", saveMode = SaveMode.ALWAYS)
public class GatewayApplication {

    public static void main(String[] args) {

        SpringApplication.run(GatewayApplication.class, args);
    }
}
