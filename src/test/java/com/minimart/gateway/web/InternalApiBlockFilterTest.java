package com.minimart.gateway.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class InternalApiBlockFilterTest {

	@Test
	void blocksBareAndPrefixedInternalPaths() {
		assertThat(InternalApiBlockFilter.isInternal("/internal/v1/orders/1/pay-result")).isTrue();
		assertThat(InternalApiBlockFilter.isInternal("/member/internal/v1/addresses/1")).isTrue();
		assertThat(InternalApiBlockFilter.isInternal("/actuator/health")).isFalse();
		assertThat(InternalApiBlockFilter.isInternal("/member/me")).isFalse();
	}
}
