package br.org.itinerario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.org.itinerario.domain.User;
import br.org.itinerario.infrastructure.UserRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.test.context.ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepository users;

    @Autowired
    PasswordEncoder passwords;

    private static final String PASSWORD = "test-password-long";

    private String student(String email) {
        return """
        {"name":"Estudante","email":"%s","password":"%s","schoolYear":1,"interests":"Lógica"}
        """.formatted(email, PASSWORD);
    }

    private String address() {
        return UUID.randomUUID().toString();
    }

    @Test
    void registrationLoginLogoutAndBlockedSession() throws Exception {
        String email = "student@school.test";
        mvc.perform(
            post("/api/auth/register/student")
                .with(csrf())
                .with(r -> {
                    r.setRemoteAddr(address());
                    return r;
                })
                .contentType("application/json")
                .content(student(email))
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("passwordHash").doesNotExist());
        var user = users.findByEmail(email).orElseThrow();
        assertThat(passwords.matches(PASSWORD, user.passwordHash)).isTrue();
        var session = (MockHttpSession) mvc
            .perform(
                post("/api/auth/login")
                    .with(csrf())
                    .param("email", email)
                    .param("password", PASSWORD)
            )
            .andExpect(status().isNoContent())
            .andReturn()
            .getRequest()
            .getSession(false);
        mvc.perform(get("/api/auth/me").session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("role").value("STUDENT"));
        mvc.perform(get("/api/admin/users").session(session)).andExpect(status().isForbidden());
        user.status = User.Status.BLOCKED;
        users.saveAndFlush(user);
        mvc.perform(get("/api/auth/me").session(session)).andExpect(status().isUnauthorized());
        mvc.perform(
            post("/api/auth/login").with(csrf()).param("email", email).param("password", PASSWORD)
        ).andExpect(status().isUnauthorized());
        user.status = User.Status.ACTIVE;
        users.saveAndFlush(user);
        var second = (MockHttpSession) mvc
            .perform(
                post("/api/auth/login")
                    .with(csrf())
                    .param("email", email)
                    .param("password", PASSWORD)
            )
            .andExpect(status().isNoContent())
            .andReturn()
            .getRequest()
            .getSession(false);
        mvc.perform(post("/api/auth/logout").session(second).with(csrf())).andExpect(
            status().isNoContent()
        );
        assertThat(second.isInvalid()).isTrue();
    }

    @Test
    void mentorIsPendingAndDuplicateEmailRejected() throws Exception {
        String body = """
        {"name":"Mentor","email":"mentor@school.test","password":"test-password-long",
         "bio":"Professor","expertiseAreas":"Lógica","availability":"Tarde","experience":"Escola"}
        """;
        mvc.perform(
            post("/api/auth/register/mentor")
                .with(csrf())
                .contentType("application/json")
                .content(body)
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("approvalStatus").value("PENDING_APPROVAL"));
        mvc.perform(
            post("/api/auth/register/student")
                .with(csrf())
                .contentType("application/json")
                .content(student("MENTOR@school.test"))
        ).andExpect(status().isConflict());
    }

    @Test
    void anonymousCsrfValidationAndRoleInjectionAreRejected() throws Exception {
        mvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/auth/csrf"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("token").isNotEmpty());
        mvc.perform(
            post("/api/auth/register/student")
                .contentType("application/json")
                .content(student("x@test.local"))
        ).andExpect(status().isForbidden());
        mvc.perform(
            post("/api/auth/register/student")
                .with(csrf())
                .contentType("application/json")
                .content(student("x@test.local").replace("\"schoolYear\":1", "\"schoolYear\":5"))
        ).andExpect(status().isBadRequest());
        mvc.perform(
            post("/api/auth/register/student")
                .with(csrf())
                .contentType("application/json")
                .content(
                    student("x@test.local").replace(
                        "\"schoolYear\":1",
                        "\"schoolYear\":1,\"role\":\"ADMIN\""
                    )
                )
        ).andExpect(status().isBadRequest());
    }

    @Test
    void authenticationRateLimited() throws Exception {
        for (int i = 0; i < 10; i++) mvc.perform(
            post("/api/auth/login")
                .with(csrf())
                .with(r -> {
                    r.setRemoteAddr("192.0.2.10");
                    return r;
                })
                .param("email", "missing@test.local")
                .param("password", "invalid")
        ).andExpect(status().isUnauthorized());
        mvc.perform(
            post("/api/auth/login")
                .with(csrf())
                .with(r -> {
                    r.setRemoteAddr("192.0.2.10");
                    return r;
                })
        ).andExpect(status().isTooManyRequests());
    }

    @Test
    void administratorCanListPendingMentorsWithoutPrivateData() throws Exception {
        var admin = new User();
        admin.name = "Admin";
        admin.email = "admin@test.local";
        admin.passwordHash = passwords.encode(PASSWORD);
        admin.role = User.Role.ADMIN;
        users.saveAndFlush(admin);
        mvc.perform(
            get("/api/admin/mentors/pending").with(
                org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(
                    admin.email
                ).roles("ADMIN")
            )
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$..passwordHash").isEmpty())
            .andExpect(jsonPath("$..email").isEmpty());
    }
}
