package com.kiranvellanki.flowguard.flowguard_core.filter;

import com.kiranvellanki.flowguard.flowguard_core.service.RateLimiterService;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

	private final RateLimiterService rateLimiterService;

	public RateLimitFilter(RateLimiterService rateLimiterService) {
		this.rateLimiterService = rateLimiterService;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		System.out.println("FlowGuard Intercepted Request");

		String apiKey = exchange.getRequest()
				.getHeaders()
				.getFirst("X-API-KEY");

		if (apiKey == null || apiKey.isBlank()) {
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		}

		if (!rateLimiterService.allow(apiKey)) {
			exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
			exchange.getResponse().getHeaders().add("Retry-After", "60");
			return exchange.getResponse().setComplete();
		}

		return chain.filter(exchange);
	}

	@Override
	public int getOrder() {
		return -1;
	}
}
