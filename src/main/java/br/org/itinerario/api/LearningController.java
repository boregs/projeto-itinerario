package br.org.itinerario.api;

import br.org.itinerario.application.*;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LearningController {

    private final LearningContentService content;
    private final LearningProgressService progress;

    public LearningController(LearningContentService content, LearningProgressService progress) {
        this.content = content;
        this.progress = progress;
    }

    @GetMapping("/tracks")
    public List<LearningContentService.TrackView> tracks(
        Principal actor,
        @RequestParam(defaultValue = "1") int page
    ) {
        return content.list(actor.getName(), page, false);
    }

    @GetMapping("/tracks/{id}")
    public LearningContentService.TrackDetail track(Principal actor, @PathVariable UUID id) {
        return content.track(actor.getName(), id);
    }

    @GetMapping("/modules/{id}")
    public LearningContentService.ModuleDetail module(Principal actor, @PathVariable UUID id) {
        return content.module(actor.getName(), id);
    }

    @GetMapping("/activities/{id}")
    public LearningContentService.ActivityView activity(Principal actor, @PathVariable UUID id) {
        return content.activity(actor.getName(), id);
    }

    @PostMapping("/activities/{id}/attempt")
    @ResponseStatus(HttpStatus.CREATED)
    public LearningProgressService.Feedback attempt(
        Principal actor,
        @PathVariable UUID id,
        @Valid @RequestBody LearningProgressService.AttemptInput input
    ) {
        return progress.attempt(actor.getName(), id, input);
    }

    @GetMapping("/progress/me")
    public LearningProgressService.Progress progress(
        Principal actor,
        @RequestParam(defaultValue = "1") int page
    ) {
        return progress.own(actor.getName(), page);
    }

    @GetMapping("/achievements/me")
    public List<LearningProgressService.AchievementView> achievements(Principal actor) {
        return progress.achievements(actor.getName());
    }

    @GetMapping("/attempts/me")
    public List<LearningProgressService.AttemptView> attempts(
        Principal actor,
        @RequestParam(defaultValue = "1") int page
    ) {
        return progress.attempts(actor.getName(), page);
    }

    @GetMapping("/admin/tracks")
    public List<LearningContentService.TrackView> adminTracks(
        Principal actor,
        @RequestParam(defaultValue = "1") int page
    ) {
        return content.list(actor.getName(), page, true);
    }

    @PostMapping("/admin/tracks")
    @ResponseStatus(HttpStatus.CREATED)
    public LearningContentService.TrackView createTrack(
        Principal actor,
        @Valid @RequestBody LearningContentService.TrackInput input
    ) {
        return content.saveTrack(actor.getName(), null, input);
    }

    @PutMapping("/admin/tracks/{id}")
    public LearningContentService.TrackView updateTrack(
        Principal actor,
        @PathVariable UUID id,
        @Valid @RequestBody LearningContentService.TrackInput input
    ) {
        return content.saveTrack(actor.getName(), id, input);
    }

    @DeleteMapping("/admin/tracks/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archiveTrack(Principal actor, @PathVariable UUID id) {
        content.archiveTrack(actor.getName(), id);
    }

    @GetMapping("/admin/tracks/{id}/modulos")
    public List<LearningContentService.ModuleView> adminModules(
        Principal actor,
        @PathVariable UUID id
    ) {
        return content.adminModules(actor.getName(), id);
    }

    @GetMapping("/admin/activities")
    public List<LearningContentService.AdminActivityView> adminActivities(
        Principal actor,
        @RequestParam UUID moduleId
    ) {
        return content.adminActivities(actor.getName(), moduleId);
    }

    @PostMapping("/admin/modules")
    @ResponseStatus(HttpStatus.CREATED)
    public LearningContentService.ModuleView createModule(
        Principal actor,
        @Valid @RequestBody LearningContentService.ModuleInput input
    ) {
        return content.saveModule(actor.getName(), null, input);
    }

    @PutMapping("/admin/modules/{id}")
    public LearningContentService.ModuleView updateModule(
        Principal actor,
        @PathVariable UUID id,
        @Valid @RequestBody LearningContentService.ModuleInput input
    ) {
        return content.saveModule(actor.getName(), id, input);
    }

    @PostMapping("/admin/activities")
    @ResponseStatus(HttpStatus.CREATED)
    public LearningContentService.AdminActivityView createActivity(
        Principal actor,
        @Valid @RequestBody LearningContentService.ActivityInput input
    ) {
        return content.saveActivity(actor.getName(), null, input);
    }

    @PutMapping("/admin/activities/{id}")
    public LearningContentService.AdminActivityView updateActivity(
        Principal actor,
        @PathVariable UUID id,
        @Valid @RequestBody LearningContentService.ActivityInput input
    ) {
        return content.saveActivity(actor.getName(), id, input);
    }
}
