package com.kiranvellanki.flowguard.flowguard_core.filter;

import com.kiranvellanki.flowguard.flowguard_core.model.RateLimitDecision;
import com.kiranvellanki.flowguard.flowguard_core.service.RateLimiterService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RateLimitFilterTests {

	@Test
	void rejectsRequestsWithoutApiKey() {
		RateLimitFilter filter = new RateLimitFilter(clientId -> RateLimitDecision.allowed(5, 4));
		MockServerWebExchange exchange = MockServerWebExchange.from(
				MockServerHttpRequest.get("/cartService/items").build()
		);

		filter.filter(exchange, ignored -> Mono.empty()).block();

		assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
	}

	@Test
	void skipsAdminRequests() {
		AtomicInteger calls = new AtomicInteger();
		RateLimitFilter filter = new RateLimitFilter(clientId -> {
			calls.incrementAndGet();
			return RateLimitDecision.denied(0, 60);
		});
		MockServerWebExchange exchange = MockServerWebExchange.from(
				MockServerHttpRequest.get("/admin/rules").build()
		);

		filter.filter(exchange, ignored -> Mono.empty()).block();

		assertEquals(0, calls.get());
	}

	@Test
	void allowsFiveRequestsAndRejectsTheSixthForClientA() {
		AtomicInteger allowedRequests = new AtomicInteger();
		RateLimitFilter filter = new RateLimitFilter(new CountingRateLimiterService(5));

		for (int i = 0; i < 5; i++) {
			MockServerWebExchange exchange = exchangeWithApiKey("clientA");

			filter.filter(exchange, ignored -> {
				allowedRequests.incrementAndGet();
				return Mono.empty();
			}).block();
		}

		MockServerWebExchange sixthRequest = exchangeWithApiKey("clientA");
		filter.filter(sixthRequest, ignored -> Mono.empty()).block();

		assertEquals(5, allowedRequests.get());
		assertEquals(HttpStatus.TOO_MANY_REQUESTS, sixthRequest.getResponse().getStatusCode());
		assertEquals("60", sixthRequest.getResponse().getHeaders().getFirst("Retry-After"));
		assertEquals("5", sixthRequest.getResponse().getHeaders().getFirst("X-RateLimit-Limit"));
		assertEquals("0", sixthRequest.getResponse().getHeaders().getFirst("X-RateLimit-Remaining"));
	}

	private MockServerWebExchange exchangeWithApiKey(String apiKey) {
		ServerWebExchange exchange = MockServerWebExchange.from(
				MockServerHttpRequest.get("/cartService/items")
						.header("X-API-KEY", apiKey)
						.build()
		);

		return (MockServerWebExchange) exchange;
	}

	private static class CountingRateLimiterService implements RateLimiterService {

		private final int limit;

		private final AtomicInteger count = new AtomicInteger();

		private CountingRateLimiterService(int limit) {
			this.limit = limit;
		}

		@Override
		public RateLimitDecision check(String clientId) {
			int currentCount = count.incrementAndGet();
			if (currentCount > limit) {
				return RateLimitDecision.denied(limit, 60);
			}

			return RateLimitDecision.allowed(limit, limit - currentCount);
		}
	}
}
