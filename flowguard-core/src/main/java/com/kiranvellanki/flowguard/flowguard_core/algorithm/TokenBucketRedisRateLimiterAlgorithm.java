package com.kiranvellanki.flowguard.flowguard_core.algorithm;

import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitAlgorithmType;
import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitDecision;
import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitRule;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TokenBucketRedisRateLimiterAlgorithm implements RateLimiterAlgorithm {

	private static final DefaultRedisScript<List> TOKEN_BUCKET_SCRIPT = new DefaultRedisScript<>(
			"""
			local tokensKey = KEYS[1]
			local timestampKey = KEYS[2]
			local capacity = tonumber(ARGV[1])
			local refillPerSecond = tonumber(ARGV[2])
			local now = tonumber(ARGV[3])
			local ttlSeconds = tonumber(ARGV[4])

			local currentTokens = tonumber(redis.call('GET', tokensKey))
			if currentTokens == nil then
				currentTokens = capacity
			end

			local lastRefill = tonumber(redis.call('GET', timestampKey))
			if lastRefill == nil then
				lastRefill = now
			end

			local elapsedSeconds = math.max((now - lastRefill) / 1000, 0)
			local refreshedTokens = math.min(capacity, currentTokens + (elapsedSeconds * refillPerSecond))
			local allowed = 0
			local retryAfterSeconds = 0

			if refreshedTokens >= 1 then
				allowed = 1
				refreshedTokens = refreshedTokens - 1
			else
				retryAfterSeconds = math.ceil((1 - refreshedTokens) / refillPerSecond)
			end

			redis.call('SET', tokensKey, refreshedTokens, 'EX', ttlSeconds)
			redis.call('SET', timestampKey, now, 'EX', ttlSeconds)

			return { allowed, math.floor(refreshedTokens), retryAfterSeconds }
			""",
			List.class
	);

	private final RedisTemplate<String, String> redisTemplate;

	public TokenBucketRedisRateLimiterAlgorithm(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public RateLimitAlgorithmType getAlgorithmType() {
		return RateLimitAlgorithmType.TOKEN_BUCKET;
	}

	@Override
	public RateLimitDecision allow(RateLimitRule rule) {
		String tokensKey = "tb:" + rule.clientId() + ":tokens";
		String timestampKey = "tb:" + rule.clientId() + ":timestamp";
		double refillPerSecond = (double) rule.limit() / rule.windowSeconds();
		long ttlSeconds = Math.max(rule.windowSeconds() * 2L, 1L);

		List<Long> result = redisTemplate.execute(
				TOKEN_BUCKET_SCRIPT,
				List.of(tokensKey, timestampKey),
				String.valueOf(rule.limit()),
				String.valueOf(refillPerSecond),
				String.valueOf(System.currentTimeMillis()),
				String.valueOf(ttlSeconds)
		);

		if (result == null || result.size() < 3) {
			throw new IllegalStateException("Redis did not return token bucket data for " + rule.clientId());
		}

		boolean allowed = result.get(0) == 1;
		long remaining = result.get(1);
		long retryAfterSeconds = result.get(2);

		if (!allowed) {
			return RateLimitDecision.denied(rule.limit(), retryAfterSeconds);
		}

		return RateLimitDecision.allowed(rule.limit(), remaining);
	}
}
