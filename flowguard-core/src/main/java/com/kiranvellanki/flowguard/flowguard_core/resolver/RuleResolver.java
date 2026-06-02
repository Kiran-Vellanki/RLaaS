package com.kiranvellanki.flowguard.flowguard_core.resolver;

import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitRule;

import java.util.Optional;

public interface RuleResolver {

	Optional<RateLimitRule> getRule(String clientId);
}
