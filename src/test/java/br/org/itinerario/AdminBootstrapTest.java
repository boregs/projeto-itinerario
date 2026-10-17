package br.org.itinerario;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import br.org.itinerario.domain.User;
import br.org.itinerario.infrastructure.*;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

class AdminBootstrapTest {

    @Test
    void createsHashedAdministratorOnlyInEmptyDatabaseWithoutHttpServer() throws Exception {
        var users = mock(UserRepository.class);
        var audit = mock(AuditRepository.class);
        var passwords = Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
        var env = new MockEnvironment()
            .withProperty("spring.main.web-application-type", "none")
            .withProperty("BOOTSTRAP_ADMIN_EMAIL", "ADMIN@example.test")
            .withProperty("BOOTSTRAP_ADMIN_PASSWORD", "test-password-long");
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var bootstrap = new AdminBootstrap(
                users,
                audit,
                passwords,
                env,
                factory.getValidator()
            );
            bootstrap.run(new DefaultApplicationArguments());
            var captured = ArgumentCaptor.forClass(User.class);
            verify(users).saveAndFlush(captured.capture());
            assertThat(captured.getValue().role).isEqualTo(User.Role.ADMIN);
            assertThat(captured.getValue().email).isEqualTo("admin@example.test");
            assertThat(
                passwords.matches("test-password-long", captured.getValue().passwordHash)
            ).isTrue();
            verify(audit).save(any());
        }
    }

    @Test
    void refusesExistingUsersAndNeverPromotesAnAccount() {
        var users = mock(UserRepository.class);
        when(users.count()).thenReturn(1L);
        var env = new MockEnvironment()
            .withProperty("spring.main.web-application-type", "none")
            .withProperty("BOOTSTRAP_ADMIN_EMAIL", "admin@example.test")
            .withProperty("BOOTSTRAP_ADMIN_PASSWORD", "test-password-long");
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var bootstrap = new AdminBootstrap(
                users,
                mock(AuditRepository.class),
                mock(org.springframework.security.crypto.password.PasswordEncoder.class),
                env,
                factory.getValidator()
            );
            assertThatThrownBy(() -> bootstrap.run(new DefaultApplicationArguments())).isInstanceOf(
                IllegalStateException.class
            );
            verify(users, never()).saveAndFlush(any());
        }
    }

    @Test
    void refusesMissingSecretsOrWebMode() {
        var users = mock(UserRepository.class);
        var env = new MockEnvironment();
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var bootstrap = new AdminBootstrap(
                users,
                mock(AuditRepository.class),
                mock(org.springframework.security.crypto.password.PasswordEncoder.class),
                env,
                factory.getValidator()
            );
            assertThatThrownBy(() -> bootstrap.run(new DefaultApplicationArguments())).isInstanceOf(
                IllegalStateException.class
            );
            env.setProperty("spring.main.web-application-type", "none");
            assertThatThrownBy(() -> bootstrap.run(new DefaultApplicationArguments())).isInstanceOf(
                IllegalStateException.class
            );
            verifyNoInteractions(users);
        }
    }
}
