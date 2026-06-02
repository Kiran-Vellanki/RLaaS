package com.kiranvellanki.flowguard.flowguard_core.algorithm;

import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitRule;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FixedWindowRedisRateLimiterAlgorithm implements RateLimiterAlgorithm {

	private static final DefaultRedisScript<Long> FIXED_WINDOW_SCRIPT = new DefaultRedisScript<>(
			"""
			local count = redis.call('INCR', KEYS[1])
			if count == 1 then
				redis.call('EXPIRE', KEYS[1], ARGV[1])
			end
			return count
			""",
			Long.class
	);

	private final RedisTemplate<String, String> redisTemplate;

	public FixedWindowRedisRateLimiterAlgorithm(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public boolean allow(RateLimitRule rule) {
		String key = "rl:" + rule.clientId();
		Long count = redisTemplate.execute(
				FIXED_WINDOW_SCRIPT,
				List.of(key),
				String.valueOf(rule.windowSeconds())
		);

		if (count == null) {
			throw new IllegalStateException("Redis did not return a counter value for " + key);
		}

		return count <= rule.limit();
	}
}
