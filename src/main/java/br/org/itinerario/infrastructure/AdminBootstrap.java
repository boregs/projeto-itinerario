package br.org.itinerario.infrastructure;

import br.org.itinerario.application.RegistrationService;
import br.org.itinerario.domain.*;
import jakarta.validation.Validator;
import jakarta.validation.constraints.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name = "app.bootstrap-admin", havingValue = "true")
public class AdminBootstrap implements ApplicationRunner {

    private final UserRepository users;
    private final AuditRepository audit;
    private final PasswordEncoder passwords;
    private final Environment env;
    private final Validator validator;

    public AdminBootstrap(
        UserRepository users,
        AuditRepository audit,
        PasswordEncoder passwords,
        Environment env,
        Validator validator
    ) {
        this.users = users;
        this.audit = audit;
        this.passwords = passwords;
        this.env = env;
        this.validator = validator;
    }

    private record Credentials(
        @NotBlank @Email @Size(max = 254) String email,
        @NotNull @Size(min = 12, max = 128) String password
    ) {}

    @Override
    @Transactional
    public void run(ApplicationArguments arguments) {
        if (
            !"none".equalsIgnoreCase(env.getProperty("spring.main.web-application-type"))
        ) throw new IllegalStateException(
            "Execute o bootstrap com --spring.main.web-application-type=none, sem servidor HTTP."
        );
        var input = new Credentials(
            env.getProperty("BOOTSTRAP_ADMIN_EMAIL"),
            env.getProperty("BOOTSTRAP_ADMIN_PASSWORD")
        );
        if (!validator.validate(input).isEmpty()) throw new IllegalStateException(
            "Configure BOOTSTRAP_ADMIN_EMAIL e BOOTSTRAP_ADMIN_PASSWORD (12–128 caracteres)."
        );
        if (users.count() != 0) throw new IllegalStateException(
            "Bootstrap permitido somente com banco sem usuários; nenhuma conta foi alterada."
        );
        var admin = new User();
        admin.name = "Administração";
        admin.email = RegistrationService.normalizeEmail(input.email());
        admin.passwordHash = passwords.encode(input.password());
        admin.role = User.Role.ADMIN;
        users.saveAndFlush(admin);
        audit.save(new AuditEvent(admin.id, admin.id, "ADMIN_BOOTSTRAPPED"));
    }
}
