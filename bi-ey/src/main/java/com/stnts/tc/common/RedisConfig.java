package com.stnts.tc.common;

import java.net.UnknownHostException;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author liang.zhang
 */
//@Configuration
public class RedisConfig {
	
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private Integer port;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.database}")
    private Integer database;
    @Value("${spring.redis.lettuce.pool.max-idle}")
    private Integer maxIdle;
    @Value("${spring.redis.lettuce.pool.min-idle}")
    private Integer minIdle;
    @Value("${spring.redis.lettuce.pool.max-active}")
    private Integer maxTotal;
    @Value("${spring.redis.lettuce.pool.max-wait}")
    private Long maxWaitMillis;

    @SuppressWarnings("rawtypes")
    @Bean
    LettuceConnectionFactory lettuceConnectionFactory() {
        // 连接池配置
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxIdle(maxIdle == null ? 8 : maxIdle);
        poolConfig.setMinIdle(minIdle == null ? 1 : minIdle);
        poolConfig.setMaxTotal(maxTotal == null ? 8 : maxTotal);
        poolConfig.setMaxWaitMillis(maxWaitMillis == null ? 5000L : maxWaitMillis);
        LettucePoolingClientConfiguration lettucePoolingClientConfiguration = LettucePoolingClientConfiguration.builder()
                .poolConfig(poolConfig)
                .build();
        // 单机redis
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(host);
        redisConfig.setPort(port);
        redisConfig.setDatabase(database);
        System.out.println(database);
        if (password != null && !"".equals(password)) {
            redisConfig.setPassword(password);
        }

        // 哨兵redis
        // RedisSentinelConfiguration redisConfig = new RedisSentinelConfiguration();

        // 集群redis
        /*RedisClusterConfiguration redisConfig = new RedisClusterConfiguration();
        Set<RedisNode> nodeses = new HashSet<>();
        String[] hostses = nodes.split("-");
        for (String h : hostses) {
            h = h.replaceAll("\\s", "").replaceAll("\n", "");
            if (!"".equals(h)) {
                String host = h.split(":")[0];
                int port = Integer.valueOf(h.split(":")[1]);
                nodeses.add(new RedisNode(host, port));
            }
        }
        redisConfig.setClusterNodes(nodeses);
        // 跨集群执行命令时要遵循的最大重定向数量
        redisConfig.setMaxRedirects(3);
        redisConfig.setPassword(password);*/

        return new LettuceConnectionFactory(redisConfig, lettucePoolingClientConfiguration);
    }

	@Bean
	@ConditionalOnMissingBean(name="redisTemplate")
	public RedisTemplate<Object, Object> redisTemplate(
			LettuceConnectionFactory  redisConnectionFactory) 
					throws UnknownHostException{
		RedisTemplate<Object, Object> template = new RedisTemplate<Object,Object>();
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}
	
	@Bean
	@ConditionalOnMissingBean(StringRedisTemplate.class)
	public StringRedisTemplate stringRedisTemplate(
			LettuceConnectionFactory  redisConnectionFactory) 
					throws UnknownHostException{
		StringRedisTemplate template = new StringRedisTemplate();
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}
}
