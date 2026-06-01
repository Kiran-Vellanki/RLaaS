package com.kiranvellanki.flowguard.flowguard_core.service;

import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitRule;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class InMemoryRateLimiterService implements RateLimiterService {

	private final Map<String, AtomicInteger> counters = new ConcurrentHashMap<>();

	private final Map<String, RateLimitRule> rules = Map.of(
			"clientA", new RateLimitRule("clientA", 5, 60),
			"clientB", new RateLimitRule("clientB", 10, 60)
	);

	@Override
	public boolean allow(String clientId) {
		RateLimitRule rule = rules.get(clientId);
		if (rule == null) {
			throw new RuntimeException("Client not available!");
		}

		AtomicInteger count = counters.computeIfAbsent(clientId, key -> new AtomicInteger());
		return count.incrementAndGet() <= rule.getLimit();
	}
}
