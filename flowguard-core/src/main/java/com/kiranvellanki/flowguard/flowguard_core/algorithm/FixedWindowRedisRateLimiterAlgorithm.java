package com.kiranvellanki.flowguard.flowguard_core.algorithm;

import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitRule;
import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitAlgorithmType;
import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitDecision;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FixedWindowRedisRateLimiterAlgorithm implements RateLimiterAlgorithm {

	private static final DefaultRedisScript<List> FIXED_WINDOW_SCRIPT = new DefaultRedisScript<>(
			"""
					local count = redis.call('INCR', KEYS[1])
					if count == 1 then
						redis.call('EXPIRE', KEYS[1], ARGV[1])
					end
					local ttl = redis.call('TTL', KEYS[1])
					return { count, ttl }
					""",
			List.class);

	private final RedisTemplate<String, String> redisTemplate;

	public FixedWindowRedisRateLimiterAlgorithm(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public RateLimitAlgorithmType getAlgorithmType() {
		return RateLimitAlgorithmType.FIXED_WINDOW;
	}

	@Override
	public RateLimitDecision allow(RateLimitRule rule) {
		String key = "rl:" + rule.clientId();
		List<Long> result = redisTemplate.execute(
				FIXED_WINDOW_SCRIPT,
				List.of(key),
				String.valueOf(rule.windowSeconds()));

		if (result == null || result.size() < 2) {
			throw new IllegalStateException("Redis did not return a counter value for " + key);
		}

		long count = result.get(0);
		long retryAfterSeconds = Math.max(result.get(1), 0);
		long remaining = Math.max(rule.maxRequests() - count, 0);

		if (count > rule.maxRequests()) {
			return RateLimitDecision.denied(rule.maxRequests(), retryAfterSeconds);
		}

		return RateLimitDecision.allowed(rule.maxRequests(), remaining);
	}
}
