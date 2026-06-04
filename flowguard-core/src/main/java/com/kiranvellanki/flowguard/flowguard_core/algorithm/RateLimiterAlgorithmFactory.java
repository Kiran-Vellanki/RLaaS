package com.kiranvellanki.flowguard.flowguard_core.algorithm;

import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitAlgorithmType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class RateLimiterAlgorithmFactory {

	private final Map<RateLimitAlgorithmType, RateLimiterAlgorithm> algorithms =
			new EnumMap<>(RateLimitAlgorithmType.class);

	public RateLimiterAlgorithmFactory(List<RateLimiterAlgorithm> algorithms) {
		for (RateLimiterAlgorithm algorithm : algorithms) {
			this.algorithms.put(algorithm.getAlgorithmType(), algorithm);
		}
	}

	public RateLimiterAlgorithm get(RateLimitAlgorithmType algorithmType) {
		RateLimiterAlgorithm algorithm = algorithms.get(algorithmType);
		if (algorithm == null) {
			throw new IllegalArgumentException("Unsupported rate limit algorithm: " + algorithmType);
		}
		return algorithm;
	}
}
