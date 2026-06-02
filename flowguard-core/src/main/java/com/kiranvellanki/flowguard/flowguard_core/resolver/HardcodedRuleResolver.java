package com.kiranvellanki.flowguard.flowguard_core.resolver;

import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitRule;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class HardcodedRuleResolver implements RuleResolver {

	private final Map<String, RateLimitRule> rules = Map.of(
			"clientA", new RateLimitRule("clientA", 5, 60),
			"clientB", new RateLimitRule("clientB", 10, 60)
	);

	@Override
	public Optional<RateLimitRule> getRule(String clientId) {
		return Optional.ofNullable(rules.get(clientId));
	}
}
