package com.kiranvellanki.flowguard.flowguard_core.model;

public record RateLimitRule(String clientId, RateLimitAlgorithmType algorithm, int maxRequests, int windowSeconds) {
}
