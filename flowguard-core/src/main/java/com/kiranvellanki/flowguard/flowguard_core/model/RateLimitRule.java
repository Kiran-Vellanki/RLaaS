package com.kiranvellanki.flowguard.flowguard_core.model;

public record RateLimitRule(String clientId, int limit, int windowSeconds) {
}
