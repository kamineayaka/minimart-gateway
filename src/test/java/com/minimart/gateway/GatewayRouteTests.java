package com.minimart.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.server.mvc.config.GatewayMvcProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import com.minimart.api.gateway.PublicRoutePrefixes;
import com.minimart.api.http.CorrelationHeaders;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class GatewayRouteTests {

	@Autowired
	TestRestTemplate rest;

	@Autowired
	GatewayMvcProperties gatewayMvcProperties;

	@Test
	void yamlDeclaresLbRoutesForAllFourServices() {
		assertThat(gatewayMvcProperties.getRoutes()).extracting(route -> route.getUri().toString())
				.containsExactlyInAnyOrder(
						"lb://member-service",
						"lb://product-service",
						"lb://order-service",
						"lb://payment-service");
	}

	@Test
	void healthEchoesCorrelationId() {
		RequestEntity<Void> request = RequestEntity.get("/actuator/health")
				.header(CorrelationHeaders.CORRELATION_ID, "edge-1")
				.build();
		ResponseEntity<String> response = rest.exchange(request, String.class);
		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		assertThat(response.getHeaders().getFirst(CorrelationHeaders.CORRELATION_ID)).isEqualTo("edge-1");
	}

	@Test
	void publicPrefixesRouteToLoadBalancer() {
		for (String prefix : new String[] {
				PublicRoutePrefixes.MEMBER,
				PublicRoutePrefixes.PRODUCT,
				PublicRoutePrefixes.ORDER,
				PublicRoutePrefixes.PAYMENT }) {
			ResponseEntity<String> response = rest.getForEntity(prefix + "/ping", String.class);
			assertThat(response.getStatusCode())
					.as("routed prefix %s should not be a resource 404", prefix)
					.isNotEqualTo(HttpStatus.NOT_FOUND);
			assertThat(response.getStatusCode().value()).isIn(500, 503);
		}
	}

	@Test
	void internalFeignPathsAreNotPublic() {
		assertThat(rest.postForEntity("/internal/v1/stock/reservations", null, String.class).getStatusCode())
				.isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(rest.getForEntity("/member/internal/v1/addresses/1", String.class).getStatusCode())
				.isEqualTo(HttpStatus.NOT_FOUND);
	}
}
