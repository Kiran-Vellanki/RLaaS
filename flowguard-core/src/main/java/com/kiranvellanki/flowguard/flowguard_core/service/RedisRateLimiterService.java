package com.kiranvellanki.flowguard.flowguard_core.service;

import com.kiranvellanki.flowguard.flowguard_core.algorithm.RateLimiterAlgorithm;
import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitRule;
import com.kiranvellanki.flowguard.flowguard_core.resolver.RuleResolver;
import org.springframework.stereotype.Service;

@Service
public class RedisRateLimiterService implements RateLimiterService {

	private final RuleResolver ruleResolver;

	private final RateLimiterAlgorithm rateLimiterAlgorithm;

	public RedisRateLimiterService(RuleResolver ruleResolver, RateLimiterAlgorithm rateLimiterAlgorithm) {
		this.ruleResolver = ruleResolver;
		this.rateLimiterAlgorithm = rateLimiterAlgorithm;
	}

	@Override
	public boolean allow(String clientId) {
		return ruleResolver.getRule(clientId)
				.map(rateLimiterAlgorithm::allow)
				.orElse(false);
	}
}
