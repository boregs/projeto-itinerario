package br.org.itinerario.infrastructure;

import br.org.itinerario.application.RegistrationService;
import br.org.itinerario.domain.User;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;

@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    UserDetailsService userDetailsService(UserRepository users) {
        return email -> {
            var user = users
                .findByEmail(RegistrationService.normalizeEmail(email))
                .orElseThrow(() -> new UsernameNotFoundException("Credenciais inválidas"));
            return org.springframework.security.core.userdetails.User.withUsername(user.email)
                .password(user.passwordHash)
                .roles(user.role.name())
                .disabled(user.status != User.Status.ACTIVE)
                .build();
        };
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
    SecurityFilterChain security(HttpSecurity http, UserRepository users) throws Exception {
        return http
            .authorizeHttpRequests(auth ->
                auth
                    .requestMatchers(
                        "/health",
                        "/api/auth/csrf",
                        "/api/auth/login",
                        "/api/auth/register/**"
                    )
                    .permitAll()
                    .requestMatchers("/api/admin/**")
                    .hasRole("ADMIN")
                    .anyRequest()
                    .authenticated()
            )
            .requestCache(cache -> cache.disable())
            .formLogin(form ->
                form
                    .loginProcessingUrl("/api/auth/login")
                    .usernameParameter("email")
                    .successHandler((request, response, authentication) -> response.setStatus(204))
                    .failureHandler((request, response, error) -> response.setStatus(401))
            )
            .logout(logout ->
                logout
                    .logoutUrl("/api/auth/logout")
                    .logoutSuccessHandler((request, response, authentication) ->
                        response.setStatus(204)
                    )
            )
            .exceptionHandling(errors ->
                errors
                    .authenticationEntryPoint((request, response, error) -> response.setStatus(401))
                    .accessDeniedHandler((request, response, error) -> response.setStatus(403))
            )
            .addFilterBefore(new AuthRateLimitFilter(), CsrfFilter.class)
            .addFilterAfter(new ActiveUserFilter(users), UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
