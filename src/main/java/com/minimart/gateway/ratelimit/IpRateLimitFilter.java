package com.minimart.gateway.ratelimit;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * In-memory per-IP limiter. Redis token-bucket can replace this later; rate limit stays on gateway only.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class IpRateLimitFilter extends OncePerRequestFilter {

	private final RateLimitProperties properties;

	private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

	public IpRateLimitFilter(RateLimitProperties properties) {
		this.properties = properties;
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return !properties.enabled();
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String ip = clientIp(request);
		long now = System.currentTimeMillis();
		Window window = windows.compute(ip, (key, existing) -> {
			if (existing == null || now - existing.startedAtMillis >= 1_000) {
				return new Window(now);
			}
			existing.count.incrementAndGet();
			return existing;
		});
		if (window.count.get() > properties.permitsPerSecond()) {
			response.sendError(HttpStatus.TOO_MANY_REQUESTS.value());
			return;
		}
		filterChain.doFilter(request, response);
	}

	private static String clientIp(HttpServletRequest request) {
		String forwarded = request.getHeader("X-Forwarded-For");
		if (forwarded != null && !forwarded.isBlank()) {
			return forwarded.split(",")[0].trim();
		}
		return request.getRemoteAddr() == null ? "unknown" : request.getRemoteAddr();
	}

	private static final class Window {

		private final long startedAtMillis;

		private final AtomicInteger count = new AtomicInteger(1);

		private Window(long startedAtMillis) {
			this.startedAtMillis = startedAtMillis;
		}
	}
}
