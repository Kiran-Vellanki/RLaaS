package com.kiranvellanki.flowguard.flowguard_core.model;

public record RateLimitDecision(boolean allowed, int limit, long remaining, long retryAfterSeconds) {

	public static RateLimitDecision allowed(int limit, long remaining) {
		return new RateLimitDecision(true, limit, remaining, 0);
	}

	public static RateLimitDecision denied(int limit, long retryAfterSeconds) {
		return new RateLimitDecision(false, limit, 0, retryAfterSeconds);
	}
}
