package com.stnts.bi.gateway.conf;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@AutoConfigureAfter({ RedisAutoConfiguration.class })
@AutoConfigureBefore({ RedisReactiveAutoConfiguration.class })
public class RedisConfiguration {

	@Bean(name = { "reactiveRedisTemplate" })
	public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(
			ReactiveRedisConnectionFactory reactiveRedisConnectionFactory, ResourceLoader resourceLoader) {
		RedisSerializationContext<String, Object> serializationContext = RedisSerializationContext.<String, Object>newSerializationContext()
				.key(new StringRedisSerializer())
				.value(new GenericJackson2JsonRedisSerializer())
				.hashKey(new StringRedisSerializer())
				.hashValue(new GenericJackson2JsonRedisSerializer()).build();
		return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, serializationContext);
	}

	@Bean
	public RedisSerializationContext.SerializationPair<String> keyPair() {
		return RedisSerializationContext.SerializationPair
				.fromSerializer(new StringRedisSerializer());
	}

	@Bean
	public RedisSerializationContext.SerializationPair<Object> valuePair() {
		return RedisSerializationContext.SerializationPair
				.fromSerializer(new GenericJackson2JsonRedisSerializer());
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory factory) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
		redisTemplate.setConnectionFactory((RedisConnectionFactory) factory);
		StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
		redisTemplate.setKeySerializer(stringRedisSerializer);
		redisTemplate.setHashKeySerializer(stringRedisSerializer);
		redisTemplate.setValueSerializer(stringRedisSerializer);
		redisTemplate.setHashValueSerializer(stringRedisSerializer);
		return redisTemplate;
	}
}
