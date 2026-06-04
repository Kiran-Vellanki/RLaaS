package com.kiranvellanki.flowguard.flowguard_core.repository;

import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitRule;

import java.util.Collection;
import java.util.Optional;

public interface RuleRepository {

	RateLimitRule save(RateLimitRule rule);

	Optional<RateLimitRule> findByClientId(String clientId);

	Collection<RateLimitRule> findAll();

	boolean deleteByClientId(String clientId);
}
