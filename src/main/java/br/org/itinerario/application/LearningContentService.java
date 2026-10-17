package br.org.itinerario.application;

import br.org.itinerario.domain.*;
import br.org.itinerario.infrastructure.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.*;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LearningContentService {

    private final LearningTrackRepository tracks;
    private final LearningModuleRepository modules;
    private final ActivityRepository activities;
    private final AttemptRepository attempts;
    private final AccountAccess access;
    private final AuditRepository audit;
    private final ObjectMapper json;

    public LearningContentService(
        LearningTrackRepository tracks,
        LearningModuleRepository modules,
        ActivityRepository activities,
        AttemptRepository attempts,
        AccountAccess access,
        AuditRepository audit,
        ObjectMapper json
    ) {
        this.tracks = tracks;
        this.modules = modules;
        this.activities = activities;
        this.attempts = attempts;
        this.access = access;
        this.audit = audit;
        this.json = json;
    }

    public record TrackInput(
        @NotBlank @Size(max = 150) String title,
        @NotBlank @Size(max = 1000) String description,
        @NotNull LearningStatus status,
        @Min(0) int position
    ) {}

    public record ModuleInput(
        @NotNull UUID trackId,
        @NotBlank @Size(max = 150) String title,
        @NotBlank @Size(max = 10000) String content,
        @NotNull LearningStatus status,
        @Min(0) int position
    ) {}

    public record ActivityInput(
        @NotNull UUID moduleId,
        @NotBlank @Size(max = 150) String title,
        @NotNull Activity.Type type,
        @NotBlank @Size(max = 2000) String question,
        @NotNull @Size(min = 2, max = 6) List<@NotBlank @Size(max = 500) String> options,
        @Min(0) @Max(5) int correctAnswer,
        @NotBlank @Size(max = 2000) String explanation,
        @Min(0) @Max(100) int points,
        @NotNull LearningStatus status,
        @Min(0) int position
    ) {}

    public record TrackView(
        UUID id,
        String title,
        String description,
        LearningStatus status,
        int position
    ) {}

    public record ModuleView(
        UUID id,
        UUID trackId,
        String title,
        String content,
        LearningStatus status,
        int position
    ) {}

    public record ActivityView(
        UUID id,
        UUID moduleId,
        String title,
        Activity.Type type,
        String question,
        List<String> options,
        int points,
        int position
    ) {}

    public record AdminActivityView(
        ActivityView activity,
        int correctAnswer,
        String explanation,
        LearningStatus status
    ) {}

    public record TrackDetail(TrackView track, List<ModuleView> modules) {}

    public record ModuleDetail(ModuleView module, List<ActivityView> activities) {}

    public static Pageable page(int number) {
        return PageRequest.of(
            Math.max(1, Math.min(number, 10000)) - 1,
            20,
            Sort.by("position").and(Sort.by("id"))
        );
    }

    private TrackView view(LearningTrack track) {
        return new TrackView(
            track.id,
            track.title,
            track.description,
            track.status,
            track.position
        );
    }

    private ModuleView view(LearningModule module) {
        return new ModuleView(
            module.id,
            module.trackId,
            module.title,
            module.content,
            module.status,
            module.position
        );
    }

    public List<String> options(Activity activity) {
        try {
            return json.readValue(activity.optionsJson, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException error) {
            throw new IllegalStateException("Opções da atividade inválidas.");
        }
    }

    public ActivityView view(Activity activity) {
        return new ActivityView(
            activity.id,
            activity.moduleId,
            activity.title,
            activity.type,
            activity.question,
            options(activity),
            activity.points,
            activity.position
        );
    }

    public LearningTrack activeTrack(UUID id) {
        var track = tracks.findById(id).orElseThrow(BusinessException::notFound);
        if (track.status != LearningStatus.ACTIVE) throw BusinessException.notFound();
        return track;
    }

    public LearningModule activeModule(UUID id) {
        var module = modules.findById(id).orElseThrow(BusinessException::notFound);
        activeTrack(module.trackId);
        if (module.status != LearningStatus.ACTIVE) throw BusinessException.notFound();
        return module;
    }

    public Activity activeActivity(UUID id) {
        var activity = activities.findById(id).orElseThrow(BusinessException::notFound);
        activeModule(activity.moduleId);
        if (activity.status != LearningStatus.ACTIVE) throw BusinessException.notFound();
        return activity;
    }

    @Transactional(readOnly = true)
    public List<TrackView> list(String actor, int page, boolean admin) {
        access.role(actor, admin ? User.Role.ADMIN : User.Role.STUDENT);
        return (
            admin
                ? tracks.findAll(page(page))
                : tracks.findByStatus(LearningStatus.ACTIVE, page(page))
        )
            .map(this::view)
            .toList();
    }

    @Transactional(readOnly = true)
    public TrackDetail track(String actor, UUID id) {
        access.role(actor, User.Role.STUDENT);
        var track = activeTrack(id);
        return new TrackDetail(
            view(track),
            modules
                .findByTrackIdOrderByPositionAscIdAsc(id)
                .stream()
                .filter(m -> m.status == LearningStatus.ACTIVE)
                .map(this::view)
                .toList()
        );
    }

    @Transactional(readOnly = true)
    public ModuleDetail module(String actor, UUID id) {
        access.role(actor, User.Role.STUDENT);
        var module = activeModule(id);
        return new ModuleDetail(
            view(module),
            activities
                .findByModuleIdOrderByPositionAscIdAsc(id)
                .stream()
                .filter(a -> a.status == LearningStatus.ACTIVE)
                .map(this::view)
                .toList()
        );
    }

    @Transactional(readOnly = true)
    public ActivityView activity(String actor, UUID id) {
        access.role(actor, User.Role.STUDENT);
        return view(activeActivity(id));
    }

    @Transactional(readOnly = true)
    public List<ModuleView> adminModules(String actor, UUID trackId) {
        access.role(actor, User.Role.ADMIN);
        if (!tracks.existsById(trackId)) throw BusinessException.notFound();
        return modules
            .findByTrackIdOrderByPositionAscIdAsc(trackId)
            .stream()
            .map(this::view)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<AdminActivityView> adminActivities(String actor, UUID moduleId) {
        access.role(actor, User.Role.ADMIN);
        if (!modules.existsById(moduleId)) throw BusinessException.notFound();
        return activities
            .findByModuleIdOrderByPositionAscIdAsc(moduleId)
            .stream()
            .map(a -> new AdminActivityView(view(a), a.correctAnswer, a.explanation, a.status))
            .toList();
    }

    public TrackView saveTrack(String actor, UUID id, TrackInput input) {
        var admin = access.role(actor, User.Role.ADMIN);
        var track =
            id == null
                ? new LearningTrack()
                : tracks.findById(id).orElseThrow(BusinessException::notFound);
        track.title = input.title().strip();
        track.description = input.description().strip();
        track.status = input.status();
        track.position = input.position();
        tracks.save(track);
        audit.save(
            new AuditEvent(admin.id, track.id, id == null ? "TRACK_CREATED" : "TRACK_UPDATED")
        );
        return view(track);
    }

    public void archiveTrack(String actor, UUID id) {
        var admin = access.role(actor, User.Role.ADMIN);
        var track = tracks.findById(id).orElseThrow(BusinessException::notFound);
        track.status = LearningStatus.INACTIVE;
        audit.save(new AuditEvent(admin.id, id, "TRACK_ARCHIVED"));
    }

    public ModuleView saveModule(String actor, UUID id, ModuleInput input) {
        var admin = access.role(actor, User.Role.ADMIN);
        if (!tracks.existsById(input.trackId())) throw BusinessException.notFound();
        var module =
            id == null
                ? new LearningModule()
                : modules.findById(id).orElseThrow(BusinessException::notFound);
        if (id != null && !module.trackId.equals(input.trackId())) throw BusinessException.conflict(
            "Não é permitido mover módulos entre trilhas."
        );
        module.trackId = input.trackId();
        module.title = input.title().strip();
        module.content = input.content().strip();
        module.status = input.status();
        module.position = input.position();
        modules.save(module);
        audit.save(
            new AuditEvent(admin.id, module.id, id == null ? "MODULE_CREATED" : "MODULE_UPDATED")
        );
        return view(module);
    }

    public AdminActivityView saveActivity(String actor, UUID id, ActivityInput input) {
        var admin = access.role(actor, User.Role.ADMIN);
        if (!modules.existsById(input.moduleId())) throw BusinessException.notFound();
        if (
            input.correctAnswer() >= input.options().size() ||
            new HashSet<>(input.options().stream().map(String::strip).toList()).size() !=
                input.options().size()
        ) throw new BusinessException(
            400,
            "Informe alternativas distintas e um índice de resposta válido."
        );
        if (
            input.type() == Activity.Type.TRUE_FALSE &&
            !input.options().equals(List.of("Verdadeiro", "Falso"))
        ) throw new BusinessException(400, "Use as opções Verdadeiro e Falso, nessa ordem.");
        var activity =
            id == null
                ? new Activity()
                : activities.lockById(id).orElseThrow(BusinessException::notFound);
        if (
            id != null && !activity.moduleId.equals(input.moduleId())
        ) throw BusinessException.conflict("Não é permitido mover atividades entre módulos.");
        if (
            id != null &&
            attempts.existsByActivityId(id) &&
            (!activity.question.equals(input.question().strip()) ||
                activity.type != input.type() ||
                !options(activity).equals(input.options()) ||
                activity.correctAnswer != input.correctAnswer() ||
                activity.points != input.points())
        ) throw BusinessException.conflict(
            "Atividade com tentativas mantém pergunta, opções, tipo, gabarito e pontos. Crie uma nova atividade para alterar a avaliação."
        );
        activity.moduleId = input.moduleId();
        activity.title = input.title().strip();
        activity.type = input.type();
        activity.question = input.question().strip();
        try {
            activity.optionsJson = json.writeValueAsString(input.options());
        } catch (JsonProcessingException error) {
            throw new BusinessException(400, "Alternativas inválidas.");
        }
        // SQL storage is bounded independently of the decoded alternatives.
        if (activity.optionsJson.length() > 5000) throw new BusinessException(
            400,
            "Alternativas excedem o limite de armazenamento."
        );
        activity.correctAnswer = input.correctAnswer();
        activity.explanation = input.explanation().strip();
        activity.points = input.points();
        activity.status = input.status();
        activity.position = input.position();
        activities.save(activity);
        audit.save(
            new AuditEvent(
                admin.id,
                activity.id,
                id == null ? "ACTIVITY_CREATED" : "ACTIVITY_UPDATED"
            )
        );
        return new AdminActivityView(
            view(activity),
            activity.correctAnswer,
            activity.explanation,
            activity.status
        );
    }
}
