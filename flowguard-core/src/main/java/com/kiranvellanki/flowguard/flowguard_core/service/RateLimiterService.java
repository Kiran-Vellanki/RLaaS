package com.kiranvellanki.flowguard.flowguard_core.service;

import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitDecision;

public interface RateLimiterService {

	RateLimitDecision check(String clientId);

	default boolean allow(String clientId) {
		return check(clientId).allowed();
	}
}
