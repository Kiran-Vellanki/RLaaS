package com.kiranvellanki.flowguard.flowguard_core.algorithm;

import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitAlgorithmType;
import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitDecision;
import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitRule;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FixedWindowRedisRateLimiterAlgorithmTests {

	private final RedisTemplate<String, String> redisTemplate = mock(RedisTemplate.class);

	private final FixedWindowRedisRateLimiterAlgorithm algorithm =
			new FixedWindowRedisRateLimiterAlgorithm(redisTemplate);

	@Test
	void allowsFirstRequestAndUsesClientKeyWithWindowSeconds() {
		RateLimitRule rule = new RateLimitRule("clientA", RateLimitAlgorithmType.FIXED_WINDOW, 5, 60);
		when(redisTemplate.execute(any(RedisScript.class), eq(List.of("rl:clientA")), eq("60")))
				.thenReturn(List.of(1L, 60L));

		RateLimitDecision decision = algorithm.allow(rule);

		assertTrue(decision.allowed());
		assertEquals(4, decision.remaining());

		verify(redisTemplate).execute(any(RedisScript.class), eq(List.of("rl:clientA")), eq("60"));
	}

	@Test
	void deniesRequestWhenRedisCountIsGreaterThanRuleLimit() {
		RateLimitRule rule = new RateLimitRule("clientA", RateLimitAlgorithmType.FIXED_WINDOW, 5, 60);
		when(redisTemplate.execute(any(RedisScript.class), eq(List.of("rl:clientA")), eq("60")))
				.thenReturn(List.of(6L, 18L));

		RateLimitDecision decision = algorithm.allow(rule);

		assertFalse(decision.allowed());
		assertEquals(18, decision.retryAfterSeconds());
	}
}
