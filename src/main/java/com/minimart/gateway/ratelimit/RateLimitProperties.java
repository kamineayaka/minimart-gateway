package com.minimart.gateway.ratelimit;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "minimart.gateway.rate-limit")
public record RateLimitProperties(boolean enabled, int permitsPerSecond) {

	public RateLimitProperties {
		if (permitsPerSecond <= 0) {
			permitsPerSecond = 100;
		}
	}
}
