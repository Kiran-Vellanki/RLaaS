package com.kiranvellanki.flowguard.flowguard_core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

	@Bean
	public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.setKeySerializer(StringRedisSerializer.UTF_8);
		redisTemplate.setValueSerializer(StringRedisSerializer.UTF_8);
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}
}
