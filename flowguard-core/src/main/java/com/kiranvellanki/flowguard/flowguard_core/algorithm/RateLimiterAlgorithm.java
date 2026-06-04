package com.kiranvellanki.flowguard.flowguard_core.algorithm;

import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitRule;
import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitAlgorithmType;
import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitDecision;

public interface RateLimiterAlgorithm {

	RateLimitAlgorithmType getAlgorithmType();

	RateLimitDecision allow(RateLimitRule rule);
}
