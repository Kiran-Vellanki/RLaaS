package com.kiranvellanki.flowguard.flowguard_core.service;

public interface RateLimiterService {

	boolean allow(String clientId);
}
