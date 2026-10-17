package br.org.itinerario.infrastructure;

import com.github.benmanes.caffeine.cache.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.web.filter.OncePerRequestFilter;

final class AuthRateLimitFilter extends OncePerRequestFilter {

    private final Cache<String, AtomicInteger> attempts = Caffeine.newBuilder()
        .maximumSize(10000)
        .expireAfterWrite(Duration.ofMinutes(1))
        .build();

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain
    ) throws ServletException, IOException {
        var path = request.getRequestURI().substring(request.getContextPath().length());
        if (
            "POST".equals(request.getMethod()) &&
            (path.equals("/api/auth/login") || path.startsWith("/api/auth/register/")) &&
            attempts.get(request.getRemoteAddr(), key -> new AtomicInteger()).incrementAndGet() > 10
        ) {
            response.setStatus(429);
            response.setHeader("Retry-After", "60");
            return;
        }
        chain.doFilter(request, response);
    }
}
