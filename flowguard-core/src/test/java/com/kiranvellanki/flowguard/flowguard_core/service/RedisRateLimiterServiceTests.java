package com.kiranvellanki.flowguard.flowguard_core.service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.kiranvellanki.flowguard.flowguard_core.algorithm.RateLimiterAlgorithm;
import com.kiranvellanki.flowguard.flowguard_core.algorithm.RateLimiterAlgorithmFactory;
import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitAlgorithmType;
import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitDecision;
import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitRule;
import com.kiranvellanki.flowguard.flowguard_core.resolver.RuleResolver;

class RedisRateLimiterServiceTests {

	@Test
	void resolvesRuleBeforeCallingAlgorithm() {
		RateLimitRule expectedRule = new RateLimitRule("clientA", RateLimitAlgorithmType.FIXED_WINDOW, 5, 60);
		AtomicReference<RateLimitRule> ruleUsedByAlgorithm = new AtomicReference<>();
		RuleResolver ruleResolver = clientId -> Optional.of(expectedRule);
		RateLimiterAlgorithm algorithm = new RateLimiterAlgorithm() {
			@Override
			public RateLimitAlgorithmType getAlgorithmType() {
				return RateLimitAlgorithmType.FIXED_WINDOW;
			}

			@Override
			public RateLimitDecision allow(RateLimitRule rule) {
				ruleUsedByAlgorithm.set(rule);
				return RateLimitDecision.allowed(rule.maxRequests(), 4);
			}
		};

		RedisRateLimiterService service = new RedisRateLimiterService(
				ruleResolver,
				new RateLimiterAlgorithmFactory(java.util.List.of(algorithm)));

		assertTrue(service.allow("clientA"));
		assertEquals(expectedRule, ruleUsedByAlgorithm.get());
	}

	@Test
	void deniesClientsWithoutRules() {
		RedisRateLimiterService service = new RedisRateLimiterService(
				clientId -> Optional.empty(),
				new RateLimiterAlgorithmFactory(java.util.List.of()));

		assertFalse(service.allow("unknownClient"));
	}
}
