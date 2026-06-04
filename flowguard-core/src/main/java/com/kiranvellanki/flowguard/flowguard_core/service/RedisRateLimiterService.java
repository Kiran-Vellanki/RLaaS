package com.kiranvellanki.flowguard.flowguard_core.service;

import com.kiranvellanki.flowguard.flowguard_core.algorithm.RateLimiterAlgorithmFactory;
import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitDecision;
import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitRule;
import com.kiranvellanki.flowguard.flowguard_core.resolver.RuleResolver;
import org.springframework.stereotype.Service;

@Service
public class RedisRateLimiterService implements RateLimiterService {

	private final RuleResolver ruleResolver;

	private final RateLimiterAlgorithmFactory rateLimiterAlgorithmFactory;

	public RedisRateLimiterService(RuleResolver ruleResolver, RateLimiterAlgorithmFactory rateLimiterAlgorithmFactory) {
		this.ruleResolver = ruleResolver;
		this.rateLimiterAlgorithmFactory = rateLimiterAlgorithmFactory;
	}

	@Override
	public RateLimitDecision check(String clientId) {
		return ruleResolver.getRule(clientId)
				.map(this::check)
				.orElse(RateLimitDecision.denied(0, 60));
	}

	private RateLimitDecision check(RateLimitRule rule) {
		return rateLimiterAlgorithmFactory.get(rule.algorithm()).allow(rule);
	}
}
