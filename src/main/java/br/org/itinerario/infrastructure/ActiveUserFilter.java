package br.org.itinerario.infrastructure;

import br.org.itinerario.domain.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

final class ActiveUserFilter extends OncePerRequestFilter {

    private final UserRepository users;

    ActiveUserFilter(UserRepository users) {
        this.users = users;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain
    ) throws ServletException, IOException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (
            authentication != null &&
            authentication.isAuthenticated() &&
            !(authentication instanceof AnonymousAuthenticationToken)
        ) {
            var user = users.findByEmail(authentication.getName());
            if (
                user.isEmpty() ||
                user.get().status != User.Status.ACTIVE ||
                authentication
                    .getAuthorities()
                    .stream()
                    .noneMatch(authority ->
                        authority.getAuthority().equals("ROLE_" + user.get().role.name())
                    )
            ) {
                SecurityContextHolder.clearContext();
                var session = request.getSession(false);
                if (session != null) session.invalidate();
                response.setStatus(401);
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
