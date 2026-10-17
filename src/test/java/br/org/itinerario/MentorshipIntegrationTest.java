package br.org.itinerario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.org.itinerario.domain.*;
import br.org.itinerario.infrastructure.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MentorshipIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper json;

    @Autowired
    UserRepository users;

    @Autowired
    MentorshipRequestRepository requests;

    @Autowired
    MentorshipRepository sessions;

    @Autowired
    AuditRepository audit;

    User student, otherStudent, mentor, otherMentor, admin;

    @BeforeEach
    void setup() {
        student = account(User.Role.STUDENT, User.Approval.PENDING_APPROVAL);
        otherStudent = account(User.Role.STUDENT, User.Approval.PENDING_APPROVAL);
        mentor = account(User.Role.MENTOR, User.Approval.PENDING_APPROVAL);
        otherMentor = account(User.Role.MENTOR, User.Approval.APPROVED);
        admin = account(User.Role.ADMIN, User.Approval.PENDING_APPROVAL);
    }

    User account(User.Role role, User.Approval approval) {
        var account = new User();
        account.name = "Conta de teste";
        account.email = UUID.randomUUID() + "@school.test";
        account.passwordHash = "unused-by-mock-authentication";
        account.role = role;
        if (role == User.Role.MENTOR) {
            var profile = new MentorProfile();
            profile.user = account;
            profile.bio = "Professor";
            profile.expertiseAreas = "Matemática / Lógica";
            profile.availability = "Manhã; Tarde";
            profile.experience = "Escola";
            profile.approvalStatus = approval;
            account.mentorProfile = profile;
        }
        if (role == User.Role.STUDENT) {
            var profile = new StudentProfile();
            profile.user = account;
            profile.schoolYear = 1;
            profile.interests = "Lógica";
            account.studentProfile = profile;
        }
        return users.saveAndFlush(account);
    }

    org.springframework.test.web.servlet.request.RequestPostProcessor as(User account) {
        return user(account.email).roles(account.role.name());
    }

    String body(Object value) throws Exception {
        return json.writeValueAsString(value);
    }

    UUID createRequest() throws Exception {
        var response = mvc
            .perform(
                post("/api/mentorship-requests")
                    .with(as(student))
                    .with(csrf())
                    .contentType("application/json")
                    .content(
                        body(
                            Map.of(
                                "subject",
                                "Entender condicionais",
                                "area",
                                " LÓGICA ",
                                "description",
                                "Preciso de exemplos",
                                "objective",
                                "Resolver um exercício",
                                "preferredSchedule",
                                "Tarde"
                            )
                        )
                    )
            )
            .andExpect(status().isCreated())
            .andReturn();
        return UUID.fromString(
            json.readTree(response.getResponse().getContentAsString()).get("id").asText()
        );
    }

    void approve() throws Exception {
        mvc.perform(
            post("/api/admin/mentors/" + mentor.id + "/approve")
                .with(as(admin))
                .with(csrf())
        ).andExpect(status().isNoContent());
    }

    UUID accept(UUID request, User actor) throws Exception {
        var response = mvc
            .perform(
                post("/api/mentorship-requests/" + request + "/accept")
                    .with(as(actor))
                    .with(csrf())
                    .contentType("application/json")
                    .content(body(Map.of("scheduledAt", Instant.now().plusSeconds(3600))))
            )
            .andExpect(status().isCreated())
            .andReturn();
        return UUID.fromString(
            json.readTree(response.getResponse().getContentAsString()).get("id").asText()
        );
    }

    @Test
    void fullWorkflowEnforcesOwnershipApprovalAndCompletion() throws Exception {
        var request = createRequest();
        mvc.perform(get("/api/mentorship-requests/available").with(as(mentor))).andExpect(
            status().isForbidden()
        );
        mvc.perform(
            post("/api/admin/mentors/" + mentor.id + "/approve")
                .with(as(student))
                .with(csrf())
        ).andExpect(status().isForbidden());
        approve();
        mvc.perform(get("/api/mentorship-requests/available").with(as(mentor)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$..email").isEmpty())
            .andExpect(jsonPath("$..studentId").isEmpty());
        var session = accept(request, mentor);
        assertThat(requests.findById(request).orElseThrow().status).isEqualTo(
            MentorshipRequest.Status.MATCHED
        );
        mvc.perform(get("/api/mentorships/" + session).with(as(otherStudent))).andExpect(
            status().isNotFound()
        );
        mvc.perform(get("/api/mentorships/" + session).with(as(otherMentor))).andExpect(
            status().isNotFound()
        );
        mvc.perform(get("/api/mentorships/" + session).with(as(admin))).andExpect(status().isOk());
        mvc.perform(
            put("/api/mentorships/" + session + "/status")
                .with(as(student))
                .with(csrf())
                .contentType("application/json")
                .content("{\"status\":\"IN_PROGRESS\"}")
        ).andExpect(status().isForbidden());
        mvc.perform(
            put("/api/mentorships/" + session + "/status")
                .with(as(mentor))
                .with(csrf())
                .contentType("application/json")
                .content("{\"status\":\"COMPLETED\",\"summary\":\"Concluído\"}")
        ).andExpect(status().isConflict());
        mvc.perform(
            put("/api/mentorships/" + session + "/status")
                .with(as(mentor))
                .with(csrf())
                .contentType("application/json")
                .content("{\"status\":\"IN_PROGRESS\"}")
        ).andExpect(status().isOk());
        mvc.perform(
            put("/api/mentorships/" + session + "/status")
                .with(as(mentor))
                .with(csrf())
                .contentType("application/json")
                .content("{\"status\":\"COMPLETED\"}")
        ).andExpect(status().isBadRequest());
        mvc.perform(
            put("/api/mentorships/" + session + "/status")
                .with(as(mentor))
                .with(csrf())
                .contentType("application/json")
                .content(
                    "{\"status\":\"COMPLETED\",\"summary\":\"Praticamos condicionais com exemplos.\"}"
                )
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("completedAt").isNotEmpty());
        assertThat(requests.findById(request).orElseThrow().status).isEqualTo(
            MentorshipRequest.Status.COMPLETED
        );
        assertThat(audit.findAll()).anyMatch(
            event -> event.targetId.equals(session) && event.action.equals("MENTORSHIP_COMPLETED")
        );
        mvc.perform(
            put("/api/mentorships/" + session + "/status")
                .with(as(mentor))
                .with(csrf())
                .contentType("application/json")
                .content("{\"status\":\"IN_PROGRESS\"}")
        ).andExpect(status().isConflict());
    }

    @Test
    void mismatchedAndBlockedAccountsCannotAccept() throws Exception {
        var request = createRequest();
        approve();
        var stored = users.findById(mentor.id).orElseThrow();
        stored.mentorProfile.expertiseAreas = "Biologia";
        users.saveAndFlush(stored);
        mvc.perform(
            post("/api/mentorship-requests/" + request + "/accept")
                .with(as(mentor))
                .with(csrf())
                .contentType("application/json")
                .content(body(Map.of("scheduledAt", Instant.now().plusSeconds(3600))))
        ).andExpect(status().isForbidden());
        mvc.perform(
            put("/api/admin/users/" + student.id + "/status")
                .with(as(admin))
                .with(csrf())
                .contentType("application/json")
                .content("{\"status\":\"BLOCKED\"}")
        ).andExpect(status().isNoContent());
        mvc.perform(
            post("/api/mentorship-requests/" + request + "/accept")
                .with(as(otherMentor))
                .with(csrf())
                .contentType("application/json")
                .content(body(Map.of("scheduledAt", Instant.now().plusSeconds(3600))))
        ).andExpect(status().isConflict());
        mvc.perform(get("/api/mentorship-requests/me").with(as(student))).andExpect(
            status().isUnauthorized()
        );
        assertThat(requests.findById(request).orElseThrow().status).isEqualTo(
            MentorshipRequest.Status.OPEN
        );
    }

    @Test
    void cancellationAndProfileChangesRespectPermissions() throws Exception {
        var request = createRequest();
        mvc.perform(
            post("/api/mentorship-requests/" + request + "/cancel")
                .with(as(otherStudent))
                .with(csrf())
        ).andExpect(status().isNotFound());
        mvc.perform(
            post("/api/mentorship-requests/" + request + "/cancel")
                .with(as(student))
                .with(csrf())
        ).andExpect(status().isNoContent());
        approve();
        mvc.perform(
            put("/api/mentor/profile")
                .with(as(mentor))
                .with(csrf())
                .contentType("application/json")
                .content(
                    body(
                        Map.of(
                            "name",
                            "Professor",
                            "bio",
                            "Ensino lógica",
                            "expertiseAreas",
                            "Lógica",
                            "availability",
                            "Tarde",
                            "experience",
                            "Escola"
                        )
                    )
                )
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("approvalStatus").value("PENDING_APPROVAL"));
        mvc.perform(get("/api/mentors/" + mentor.id).with(as(student))).andExpect(
            status().isNotFound()
        );
        mvc.perform(get("/api/mentorship-requests/available").with(as(mentor))).andExpect(
            status().isForbidden()
        );
        mvc.perform(
            put("/api/student/profile")
                .with(as(student))
                .with(csrf())
                .contentType("application/json")
                .content("{\"name\":\"Novo nome\",\"schoolYear\":2,\"interests\":\"Algoritmos\"}")
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("schoolYear").value(2));
        mvc.perform(get("/api/student/profile").with(as(mentor))).andExpect(status().isForbidden());
    }

    @Test
    void rejectAndApproveAreAuditedAndCannotBeRepeated() throws Exception {
        mvc.perform(
            post("/api/admin/mentors/" + mentor.id + "/reject")
                .with(as(admin))
                .with(csrf())
        ).andExpect(status().isNoContent());
        mvc.perform(
            post("/api/admin/mentors/" + mentor.id + "/approve")
                .with(as(admin))
                .with(csrf())
        ).andExpect(status().isConflict());
        assertThat(audit.findAll()).anyMatch(
            event ->
                event.targetId.equals(mentor.id) &&
                event.actorId.equals(admin.id) &&
                event.action.equals("MENTOR_REJECTED")
        );
        mvc.perform(
            put("/api/admin/users/" + admin.id + "/status")
                .with(as(admin))
                .with(csrf())
                .contentType("application/json")
                .content("{\"status\":\"BLOCKED\"}")
        ).andExpect(status().isConflict());
    }

    @Test
    void simultaneousAcceptanceCreatesExactlyOneSession() throws Exception {
        var request = createRequest();
        approve();
        var start = new CountDownLatch(1);
        var pool = Executors.newFixedThreadPool(2);
        try {
            List<Future<Integer>> results = new ArrayList<>();
            for (var actor : List.of(mentor, otherMentor))
                results.add(
                    pool.submit(() -> {
                        start.await();
                        return mvc
                            .perform(
                                post("/api/mentorship-requests/" + request + "/accept")
                                    .with(as(actor))
                                    .with(csrf())
                                    .contentType("application/json")
                                    .content(
                                        body(Map.of("scheduledAt", Instant.now().plusSeconds(3600)))
                                    )
                            )
                            .andReturn()
                            .getResponse()
                            .getStatus();
                    })
                );
            start.countDown();
            assertThat(
                List.of(
                    results.get(0).get(15, TimeUnit.SECONDS),
                    results.get(1).get(15, TimeUnit.SECONDS)
                )
            ).containsExactlyInAnyOrder(201, 409);
            assertThat(
                sessions
                    .findAll()
                    .stream()
                    .filter(session -> session.requestId.equals(request))
                    .count()
            ).isEqualTo(1);
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    void personalContactAndInvalidDateAreRejected() throws Exception {
        mvc.perform(
            post("/api/mentorship-requests")
                .with(as(student))
                .with(csrf())
                .contentType("application/json")
                .content(
                    body(
                        Map.of(
                            "subject",
                            "Ajuda",
                            "area",
                            "Lógica",
                            "description",
                            "Escreva para email@example.com",
                            "objective",
                            "Aprender",
                            "preferredSchedule",
                            "Tarde"
                        )
                    )
                )
        ).andExpect(status().isBadRequest());
        var request = createRequest();
        mvc.perform(
            post("/api/mentorship-requests/" + request + "/accept")
                .with(as(otherMentor))
                .with(csrf())
                .contentType("application/json")
                .content(body(Map.of("scheduledAt", Instant.now().minusSeconds(60))))
        ).andExpect(status().isBadRequest());
        mvc.perform(
            post("/api/mentorship-requests/" + request + "/accept")
                .with(as(otherMentor))
                .contentType("application/json")
                .content(body(Map.of("scheduledAt", Instant.now().plusSeconds(3600))))
        ).andExpect(status().isForbidden());
    }
}
