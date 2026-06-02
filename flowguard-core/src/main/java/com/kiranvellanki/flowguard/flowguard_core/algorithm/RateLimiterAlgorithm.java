package com.kiranvellanki.flowguard.flowguard_core.algorithm;

import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitRule;

public interface RateLimiterAlgorithm {

	boolean allow(RateLimitRule rule);
}
