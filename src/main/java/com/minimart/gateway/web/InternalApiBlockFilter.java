package com.minimart.gateway.web;

import java.io.IOException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.minimart.api.gateway.PublicRoutePrefixes;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Internal Feign paths are not public HTTP. Block them even if a public prefix would strip to /internal.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class InternalApiBlockFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (isInternal(request.getRequestURI())) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		filterChain.doFilter(request, response);
	}

	static boolean isInternal(String path) {
		if (path == null || path.isBlank()) {
			return false;
		}
		String uri = path.startsWith("/") ? path : "/" + path;
		if (uri.equals(PublicRoutePrefixes.INTERNAL) || uri.startsWith(PublicRoutePrefixes.INTERNAL + "/")) {
			return true;
		}
		int secondSlash = uri.indexOf('/', 1);
		if (secondSlash < 0) {
			return false;
		}
		String rest = uri.substring(secondSlash);
		return rest.equals(PublicRoutePrefixes.INTERNAL) || rest.startsWith(PublicRoutePrefixes.INTERNAL + "/");
	}
}
