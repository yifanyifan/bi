package com.stnts.bi.gateway.conf;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
public class SessionConfig implements BeanClassLoaderAware {

	private ClassLoader loader;

	@Bean
	public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
		return new GenericJackson2JsonRedisSerializer(objectMapper());
	}

	private ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		return mapper;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.loader = classLoader;
	}
}
