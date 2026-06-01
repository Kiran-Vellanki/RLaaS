package com.kiranvellanki.flowguard.flowguard_core.model;

public class RateLimitRule {

	private String clientId;

	private int limit;

	private int windowSeconds;

	public RateLimitRule(String clientId, int limit, int windowSeconds) {
		this.clientId = clientId;
		this.limit = limit;
		this.windowSeconds = windowSeconds;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getWindowSeconds() {
		return windowSeconds;
	}

	public void setWindowSeconds(int windowSeconds) {
		this.windowSeconds = windowSeconds;
	}
}
