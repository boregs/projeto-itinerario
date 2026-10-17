package br.org.itinerario.api;

import br.org.itinerario.application.MentorshipService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class MentorshipController {

    private final MentorshipService service;

    public MentorshipController(MentorshipService service) {
        this.service = service;
    }

    @PostMapping("/api/mentorship-requests")
    @ResponseStatus(HttpStatus.CREATED)
    public MentorshipService.RequestView create(
        Principal actor,
        @Valid @RequestBody MentorshipService.RequestInput input
    ) {
        return service.create(actor.getName(), input);
    }

    @GetMapping("/api/mentorship-requests/me")
    public List<MentorshipService.RequestView> requests(
        Principal actor,
        @RequestParam(defaultValue = "1") int page
    ) {
        return service.ownRequests(actor.getName(), page);
    }

    @GetMapping("/api/mentorship-requests/available")
    public List<MentorshipService.RequestView> available(
        Principal actor,
        @RequestParam(defaultValue = "1") int page
    ) {
        return service.available(actor.getName(), page);
    }

    @PostMapping("/api/mentorship-requests/{id}/accept")
    @ResponseStatus(HttpStatus.CREATED)
    public MentorshipService.SessionView accept(
        Principal actor,
        @PathVariable UUID id,
        @Valid @RequestBody MentorshipService.AcceptInput input
    ) {
        return service.accept(actor.getName(), id, input);
    }

    @PostMapping("/api/mentorship-requests/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(Principal actor, @PathVariable UUID id) {
        service.cancelRequest(actor.getName(), id);
    }

    @GetMapping("/api/mentorships/me")
    public List<MentorshipService.SessionView> sessions(
        Principal actor,
        @RequestParam(defaultValue = "1") int page
    ) {
        return service.ownSessions(actor.getName(), page);
    }

    @GetMapping("/api/mentorships/{id}")
    public MentorshipService.SessionView detail(Principal actor, @PathVariable UUID id) {
        return service.detail(actor.getName(), id);
    }

    @PutMapping("/api/mentorships/{id}/status")
    public MentorshipService.SessionView status(
        Principal actor,
        @PathVariable UUID id,
        @Valid @RequestBody MentorshipService.StatusInput input
    ) {
        return service.transition(actor.getName(), id, input);
    }

    @GetMapping("/api/admin/mentorships")
    public List<MentorshipService.SessionView> admin(
        Principal actor,
        @RequestParam(defaultValue = "1") int page
    ) {
        return service.all(actor.getName(), page);
    }
}
