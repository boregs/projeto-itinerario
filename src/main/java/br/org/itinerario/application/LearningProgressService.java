package br.org.itinerario.application;

import br.org.itinerario.domain.*;
import br.org.itinerario.infrastructure.*;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LearningProgressService {

    private final AccountAccess access;
    private final LearningContentService content;
    private final ActivityRepository activities;
    private final LearningModuleRepository modules;
    private final LearningTrackRepository tracks;
    private final AttemptRepository attempts;
    private final CompletionRepository completions;
    private final AchievementRepository achievements;

    public LearningProgressService(
        AccountAccess access,
        LearningContentService content,
        ActivityRepository activities,
        LearningModuleRepository modules,
        LearningTrackRepository tracks,
        AttemptRepository attempts,
        CompletionRepository completions,
        AchievementRepository achievements
    ) {
        this.access = access;
        this.content = content;
        this.activities = activities;
        this.modules = modules;
        this.tracks = tracks;
        this.attempts = attempts;
        this.completions = completions;
        this.achievements = achievements;
    }

    public record AttemptInput(@NotNull @Min(0) @Max(5) Integer answer) {}

    public record Feedback(
        UUID attemptId,
        boolean correct,
        int correctAnswer,
        String explanation,
        int awardedPoints,
        long totalPoints
    ) {}

    public record TrackProgress(
        UUID trackId,
        String title,
        int completedModules,
        int totalModules,
        int percentage
    ) {}

    public record Progress(long points, long level, List<TrackProgress> tracks) {}

    public record AchievementView(
        StudentAchievement.Code code,
        String title,
        String description,
        Instant achievedAt
    ) {}

    public record AttemptView(
        UUID id,
        UUID activityId,
        int answer,
        boolean correct,
        int score,
        Instant createdAt
    ) {}

    private TrackProgress progress(LearningTrack track, Set<UUID> completed) {
        var activeModules = modules
            .findByTrackIdOrderByPositionAscIdAsc(track.id)
            .stream()
            .filter(m -> m.status == LearningStatus.ACTIVE)
            .toList();
        int count = 0;
        for (var module : activeModules) {
            var activeActivities = activities
                .findByModuleIdOrderByPositionAscIdAsc(module.id)
                .stream()
                .filter(a -> a.status == LearningStatus.ACTIVE)
                .toList();
            if (
                !activeActivities.isEmpty() &&
                activeActivities.stream().allMatch(a -> completed.contains(a.id))
            ) count++;
        }
        return new TrackProgress(
            track.id,
            track.title,
            count,
            activeModules.size(),
            activeModules.isEmpty() ? 0 : (100 * count) / activeModules.size()
        );
    }

    private Set<UUID> completed(UUID studentId) {
        return completions
            .findByStudentId(studentId)
            .stream()
            .map(c -> c.activityId)
            .collect(Collectors.toSet());
    }

    private void award(UUID studentId, StudentAchievement.Code code) {
        if (achievements.existsByStudentIdAndCode(studentId, code)) return;
        var achievement = new StudentAchievement();
        achievement.studentId = studentId;
        achievement.code = code;
        achievements.save(achievement);
    }

    public Feedback attempt(String actor, UUID activityId, AttemptInput input) {
        // Serialize attempts for this student, including first-completion rewards.
        var student = access.lockRole(actor, User.Role.STUDENT);
        activities.lockById(activityId).orElseThrow(BusinessException::notFound);
        var activity = content.activeActivity(activityId);
        if (input.answer() >= content.options(activity).size()) throw new BusinessException(
            400,
            "Selecione uma alternativa disponível."
        );
        var attempt = new StudentActivityAttempt();
        attempt.studentId = student.id;
        attempt.activityId = activity.id;
        attempt.answer = input.answer();
        attempt.isCorrect = input.answer() == activity.correctAnswer;
        if (
            attempt.isCorrect &&
            !completions.existsByStudentIdAndActivityId(student.id, activity.id)
        ) {
            var completion = new StudentActivityCompletion();
            completion.studentId = student.id;
            completion.activityId = activity.id;
            completion.points = activity.points;
            completions.save(completion);
            attempt.score = activity.points;
            award(student.id, StudentAchievement.Code.FIRST_ACTIVITY);
        }
        attempts.save(attempt);
        var module = content.activeModule(activity.moduleId);
        var track = content.activeTrack(module.trackId);
        if (attempt.isCorrect && progress(track, completed(student.id)).percentage() == 100) award(
            student.id,
            StudentAchievement.Code.FIRST_TRACK
        );
        return new Feedback(
            attempt.id,
            attempt.isCorrect,
            activity.correctAnswer,
            activity.explanation,
            attempt.score,
            completions.points(student.id)
        );
    }

    @Transactional(readOnly = true)
    public Progress own(String actor, int page) {
        var student = access.role(actor, User.Role.STUDENT);
        var completed = completed(student.id);
        long points = completions.points(student.id);
        var progress = tracks
            .findByStatus(LearningStatus.ACTIVE, LearningContentService.page(page))
            .stream()
            .map(track -> progress(track, completed))
            .toList();
        return new Progress(points, 1 + points / 100, progress);
    }

    @Transactional(readOnly = true)
    public List<AchievementView> achievements(String actor) {
        var student = access.role(actor, User.Role.STUDENT);
        return achievements
            .findByStudentIdOrderByAchievedAtAsc(student.id)
            .stream()
            .map(
                achievement ->
                    switch (achievement.code) {
                        case FIRST_ACTIVITY -> new AchievementView(
                            achievement.code,
                            "Primeiro passo",
                            "Concluiu sua primeira atividade.",
                            achievement.achievedAt
                        );
                        case FIRST_TRACK -> new AchievementView(
                            achievement.code,
                            "Uma trilha completa",
                            "Concluiu sua primeira trilha de aprendizagem.",
                            achievement.achievedAt
                        );
                    }
            )
            .toList();
    }

    @Transactional(readOnly = true)
    public List<AttemptView> attempts(String actor, int page) {
        var student = access.role(actor, User.Role.STUDENT);
        return attempts
            .findByStudentIdOrderByCreatedAtDescIdAsc(
                student.id,
                PageRequest.of(Math.max(1, Math.min(page, 10000)) - 1, 20)
            )
            .map(a ->
                new AttemptView(a.id, a.activityId, a.answer, a.isCorrect, a.score, a.createdAt)
            )
            .toList();
    }
}
