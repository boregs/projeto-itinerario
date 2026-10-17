package br.org.itinerario;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.org.itinerario.application.LearningContentService;
import br.org.itinerario.domain.*;
import br.org.itinerario.infrastructure.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LearningIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper json;

    @Autowired
    UserRepository users;

    @Autowired
    LearningTrackRepository tracks;

    @Autowired
    LearningModuleRepository modules;

    @Autowired
    ActivityRepository activities;

    @Autowired
    AttemptRepository attempts;

    @Autowired
    CompletionRepository completions;

    @Autowired
    AchievementRepository achievements;

    @Autowired
    LearningContentService content;

    User admin, student, other;
    UUID trackId, moduleId, activityId;

    @BeforeEach
    void setup() {
        admin = account(User.Role.ADMIN);
        student = account(User.Role.STUDENT);
        other = account(User.Role.STUDENT);
        trackId = content
            .saveTrack(
                admin.email,
                null,
                new LearningContentService.TrackInput(
                    "Trilha de teste",
                    "Lógica",
                    LearningStatus.ACTIVE,
                    0
                )
            )
            .id();
        moduleId = content
            .saveModule(
                admin.email,
                null,
                new LearningContentService.ModuleInput(
                    trackId,
                    "Módulo",
                    "Uma sequência de instruções.",
                    LearningStatus.ACTIVE,
                    0
                )
            )
            .id();
        activityId = content
            .saveActivity(admin.email, null, input(LearningStatus.ACTIVE, 1))
            .activity()
            .id();
    }

    User account(User.Role role) {
        var user = new User();
        user.name = "Conta de teste";
        user.email = UUID.randomUUID() + "@test.local";
        user.passwordHash = "unused-by-mock-authentication";
        user.role = role;
        return users.saveAndFlush(user);
    }

    org.springframework.test.web.servlet.request.RequestPostProcessor as(User account) {
        return user(account.email).roles(account.role.name());
    }

    LearningContentService.ActivityInput input(LearningStatus status, int answer) {
        return new LearningContentService.ActivityInput(
            moduleId,
            "Sequência",
            Activity.Type.MULTIPLE_CHOICE,
            "Qual é o resultado de 2 + 3?",
            List.of("4", "5", "6"),
            answer,
            "2 + 3 = 5.",
            10,
            status,
            0
        );
    }

    String body(Object value) throws Exception {
        return json.writeValueAsString(value);
    }

    @Test
    void recordsAllAttemptsAndAwardsPointsAndAchievementsOnce() throws Exception {
        mvc.perform(get("/api/activities/" + activityId).with(as(student)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("correctAnswer").doesNotExist())
            .andExpect(jsonPath("explanation").doesNotExist());
        mvc.perform(get("/api/modules/" + moduleId).with(as(student)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$..correctAnswer").isEmpty());
        mvc.perform(
            post("/api/activities/" + activityId + "/attempt")
                .with(as(student))
                .with(csrf())
                .contentType("application/json")
                .content("{\"answer\":0}")
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("correct").value(false))
            .andExpect(jsonPath("awardedPoints").value(0))
            .andExpect(jsonPath("correctAnswer").value(1))
            .andExpect(jsonPath("explanation").isNotEmpty());
        mvc.perform(
            post("/api/activities/" + activityId + "/attempt")
                .with(as(student))
                .with(csrf())
                .contentType("application/json")
                .content("{\"answer\":1}")
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("awardedPoints").value(10));
        mvc.perform(
            post("/api/activities/" + activityId + "/attempt")
                .with(as(student))
                .with(csrf())
                .contentType("application/json")
                .content("{\"answer\":1}")
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("awardedPoints").value(0))
            .andExpect(jsonPath("totalPoints").value(10));
        assertThat(attempts.countByStudentIdAndActivityId(student.id, activityId)).isEqualTo(3);
        assertThat(completions.findByStudentId(student.id)).hasSize(1);
        assertThat(achievements.findByStudentIdOrderByAchievedAtAsc(student.id)).hasSize(2);
        mvc.perform(get("/api/progress/me").with(as(student)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("points").value(10))
            .andExpect(jsonPath("level").value(1));
        var progress = json.readTree(
            mvc
                .perform(get("/api/progress/me").with(as(student)))
                .andReturn()
                .getResponse()
                .getContentAsString()
        );
        var ownTrack = new ArrayList<com.fasterxml.jackson.databind.JsonNode>();
        progress.get("tracks").forEach(node -> {
            if (node.get("trackId").asText().equals(trackId.toString())) ownTrack.add(node);
        });
        assertThat(ownTrack).hasSize(1);
        assertThat(ownTrack.get(0).get("percentage").asInt()).isEqualTo(100);
        mvc.perform(get("/api/progress/me").with(as(other))).andExpect(jsonPath("points").value(0));
        mvc.perform(get("/api/attempts/me").with(as(other))).andExpect(jsonPath("$").isEmpty());
        mvc.perform(get("/api/achievements/me").with(as(other))).andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void inactiveActivityModuleAndTrackAreHiddenAndCannotBeAnswered() throws Exception {
        content.saveActivity(admin.email, activityId, input(LearningStatus.INACTIVE, 1));
        mvc.perform(get("/api/activities/" + activityId).with(as(student))).andExpect(
            status().isNotFound()
        );
        mvc.perform(
            post("/api/activities/" + activityId + "/attempt")
                .with(as(student))
                .with(csrf())
                .contentType("application/json")
                .content("{\"answer\":1}")
        ).andExpect(status().isNotFound());
        content.saveActivity(admin.email, activityId, input(LearningStatus.ACTIVE, 1));
        content.saveModule(
            admin.email,
            moduleId,
            new LearningContentService.ModuleInput(
                trackId,
                "Módulo",
                "Conteúdo",
                LearningStatus.INACTIVE,
                0
            )
        );
        mvc.perform(get("/api/activities/" + activityId).with(as(student))).andExpect(
            status().isNotFound()
        );
        content.saveModule(
            admin.email,
            moduleId,
            new LearningContentService.ModuleInput(
                trackId,
                "Módulo",
                "Conteúdo",
                LearningStatus.ACTIVE,
                0
            )
        );
        mvc.perform(
            delete("/api/admin/tracks/" + trackId)
                .with(as(admin))
                .with(csrf())
        ).andExpect(status().isNoContent());
        mvc.perform(get("/api/tracks/" + trackId).with(as(student))).andExpect(
            status().isNotFound()
        );
        mvc.perform(get("/api/modules/" + moduleId).with(as(student))).andExpect(
            status().isNotFound()
        );
        mvc.perform(get("/api/activities/" + activityId).with(as(student))).andExpect(
            status().isNotFound()
        );
        assertThat(tracks.existsById(trackId)).isTrue();
        assertThat(attempts.countByStudentIdAndActivityId(student.id, activityId)).isZero();
    }

    @Test
    void validatesAttemptsCsrfAndRejectsClientScore() throws Exception {
        mvc.perform(
            post("/api/activities/" + activityId + "/attempt")
                .with(as(student))
                .with(csrf())
                .contentType("application/json")
                .content("{\"answer\":5}")
        ).andExpect(status().isBadRequest());
        mvc.perform(
            post("/api/activities/" + activityId + "/attempt")
                .with(as(student))
                .with(csrf())
                .contentType("application/json")
                .content("{\"answer\":1,\"score\":100}")
        ).andExpect(status().isBadRequest());
        mvc.perform(
            post("/api/activities/" + activityId + "/attempt")
                .with(as(student))
                .contentType("application/json")
                .content("{\"answer\":1}")
        ).andExpect(status().isForbidden());
        mvc.perform(
            post("/api/activities/" + activityId + "/attempt")
                .with(as(admin))
                .with(csrf())
                .contentType("application/json")
                .content("{\"answer\":1}")
        ).andExpect(status().isForbidden());
        student.status = User.Status.BLOCKED;
        users.saveAndFlush(student);
        mvc.perform(
            post("/api/activities/" + activityId + "/attempt")
                .with(as(student))
                .with(csrf())
                .contentType("application/json")
                .content("{\"answer\":1}")
        ).andExpect(status().isUnauthorized());
        assertThat(attempts.countByStudentIdAndActivityId(student.id, activityId)).isZero();
    }

    @Test
    void administratorsManageContentButCannotRewriteAnAnsweredAssessment() throws Exception {
        mvc.perform(
            post("/api/admin/tracks")
                .with(as(student))
                .with(csrf())
                .contentType("application/json")
                .content(
                    body(
                        new LearningContentService.TrackInput(
                            "Outra trilha",
                            "Conteúdo",
                            LearningStatus.ACTIVE,
                            0
                        )
                    )
                )
        ).andExpect(status().isForbidden());
        mvc.perform(
            post("/api/admin/tracks")
                .with(as(admin))
                .with(csrf())
                .contentType("application/json")
                .content(
                    body(
                        new LearningContentService.TrackInput(
                            "Outra trilha",
                            "Conteúdo",
                            LearningStatus.ACTIVE,
                            0
                        )
                    )
                )
        ).andExpect(status().isCreated());
        mvc.perform(
            post("/api/admin/activities")
                .with(as(admin))
                .with(csrf())
                .contentType("application/json")
                .content(body(input(LearningStatus.ACTIVE, 5)))
        ).andExpect(status().isBadRequest());
        mvc.perform(
            post("/api/activities/" + activityId + "/attempt")
                .with(as(student))
                .with(csrf())
                .contentType("application/json")
                .content("{\"answer\":1}")
        ).andExpect(status().isCreated());
        mvc.perform(
            put("/api/admin/activities/" + activityId)
                .with(as(admin))
                .with(csrf())
                .contentType("application/json")
                .content(body(input(LearningStatus.ACTIVE, 0)))
        ).andExpect(status().isConflict());
        assertThat(activities.findById(activityId).orElseThrow().correctAnswer).isEqualTo(1);
        mvc.perform(
            put("/api/admin/activities/" + activityId)
                .with(as(admin))
                .with(csrf())
                .contentType("application/json")
                .content(body(input(LearningStatus.INACTIVE, 1)))
        ).andExpect(status().isOk());
        mvc.perform(get("/api/progress/me").with(as(student))).andExpect(
            jsonPath("points").value(10)
        );
    }

    @Test
    void simultaneousCorrectAttemptsAwardOnlyOnce() throws Exception {
        var start = new CountDownLatch(1);
        var pool = Executors.newFixedThreadPool(2);
        try {
            List<Future<Integer>> results = new ArrayList<>();
            for (int i = 0; i < 2; i++) results.add(
                pool.submit(() -> {
                    start.await();
                    var response = mvc
                        .perform(
                            post("/api/activities/" + activityId + "/attempt")
                                .with(as(student))
                                .with(csrf())
                                .contentType("application/json")
                                .content("{\"answer\":1}")
                        )
                        .andExpect(status().isCreated())
                        .andReturn();
                    return json
                        .readTree(response.getResponse().getContentAsString())
                        .get("awardedPoints")
                        .asInt();
                })
            );
            start.countDown();
            assertThat(
                List.of(
                    results.get(0).get(15, TimeUnit.SECONDS),
                    results.get(1).get(15, TimeUnit.SECONDS)
                )
            ).containsExactlyInAnyOrder(0, 10);
            assertThat(attempts.countByStudentIdAndActivityId(student.id, activityId)).isEqualTo(2);
            assertThat(completions.points(student.id)).isEqualTo(10);
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    void demoSeedIsRepeatableAndIncludesNineExercisesAndFinalChallenge() {
        var seed = new DemoLearningSeed(tracks, modules, activities, json);
        seed.run(new DefaultApplicationArguments());
        long count = activities.count();
        seed.run(new DefaultApplicationArguments());
        assertThat(activities.count()).isEqualTo(count);
        var track = tracks
            .findAll()
            .stream()
            .filter(t -> t.title.equals("Introdução à Lógica e Algoritmos"))
            .findFirst()
            .orElseThrow();
        var seededModules = modules.findByTrackIdOrderByPositionAscIdAsc(track.id);
        assertThat(seededModules).hasSize(4);
        assertThat(
            seededModules
                .stream()
                .mapToInt(m -> activities.findByModuleIdOrderByPositionAscIdAsc(m.id).size())
                .sum()
        ).isEqualTo(10);
    }
}
