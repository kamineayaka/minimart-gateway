package com.minimart.gateway.ratelimit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class IpRateLimitFilterTest {

	@Test
	void returns429AfterPermitsExhausted() throws Exception {
		IpRateLimitFilter filter = new IpRateLimitFilter(new RateLimitProperties(true, 2));
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/member/x");
		request.setRemoteAddr("10.0.0.8");
		filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());
		filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());
		MockHttpServletResponse blocked = new MockHttpServletResponse();
		filter.doFilter(request, blocked, new MockFilterChain());
		assertThat(blocked.getStatus()).isEqualTo(429);
	}
}
