package br.org.itinerario.api;

import br.org.itinerario.application.*;
import br.org.itinerario.domain.User;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private final RegistrationService registration;
    private final UserStore users;

    public AuthController(RegistrationService registration, UserStore users) {
        this.registration = registration;
        this.users = users;
    }

    public record Account(
        UUID id,
        String name,
        String email,
        User.Role role,
        User.Approval approvalStatus
    ) {
        static Account from(User user) {
            return new Account(
                user.id,
                user.name,
                user.email,
                user.role,
                user.mentorProfile == null ? null : user.mentorProfile.approvalStatus
            );
        }
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }

    @GetMapping("/api/auth/csrf")
    public Map<String, String> csrf(CsrfToken token) {
        return Map.of("token", token.getToken(), "headerName", token.getHeaderName());
    }

    @PostMapping("/api/auth/register/student")
    @ResponseStatus(HttpStatus.CREATED)
    public Account student(@Valid @RequestBody RegistrationService.StudentInput input) {
        return Account.from(registration.register(input));
    }

    @PostMapping("/api/auth/register/mentor")
    @ResponseStatus(HttpStatus.CREATED)
    public Account mentor(@Valid @RequestBody RegistrationService.MentorInput input) {
        return Account.from(registration.register(input));
    }

    @GetMapping("/api/auth/me")
    @Transactional(readOnly = true)
    public Account me(Principal principal) {
        return Account.from(users.findByEmail(principal.getName()).orElseThrow());
    }
}
