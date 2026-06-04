package com.kiranvellanki.flowguard.flowguard_core.repository;

import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitAlgorithmType;
import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitRule;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryRuleRepository implements RuleRepository {

	private final Map<String, RateLimitRule> rules = new ConcurrentHashMap<>();

	public InMemoryRuleRepository() {
		save(new RateLimitRule("clientA", RateLimitAlgorithmType.FIXED_WINDOW, 5, 60));
		save(new RateLimitRule("clientB", RateLimitAlgorithmType.TOKEN_BUCKET, 10, 60));
	}

	@Override
	public RateLimitRule save(RateLimitRule rule) {
		rules.put(rule.clientId(), rule);
		return rule;
	}

	@Override
	public Optional<RateLimitRule> findByClientId(String clientId) {
		return Optional.ofNullable(rules.get(clientId));
	}

	@Override
	public Collection<RateLimitRule> findAll() {
		return rules.values();
	}

	@Override
	public boolean deleteByClientId(String clientId) {
		return rules.remove(clientId) != null;
	}
}
