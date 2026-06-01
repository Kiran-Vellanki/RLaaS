package com.kiranvellanki.flowguard.flowguard_core.filter;

import com.kiranvellanki.flowguard.flowguard_core.service.InMemoryRateLimiterService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RateLimitFilterTests {

	private final RateLimitFilter filter = new RateLimitFilter(new InMemoryRateLimiterService());

	@Test
	void rejectsRequestsWithoutApiKey() {
		MockServerWebExchange exchange = MockServerWebExchange.from(
				MockServerHttpRequest.get("/cartService/items").build()
		);

		filter.filter(exchange, ignored -> Mono.empty()).block();

		assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
	}

	@Test
	void allowsFiveRequestsAndRejectsTheSixthForClientA() {
		AtomicInteger allowedRequests = new AtomicInteger();

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
	}

	private MockServerWebExchange exchangeWithApiKey(String apiKey) {
		ServerWebExchange exchange = MockServerWebExchange.from(
				MockServerHttpRequest.get("/cartService/items")
						.header("X-API-KEY", apiKey)
						.build()
		);

		return (MockServerWebExchange) exchange;
	}
}
