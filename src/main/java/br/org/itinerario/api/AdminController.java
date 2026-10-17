package br.org.itinerario.api;

import br.org.itinerario.application.MentorService;
import br.org.itinerario.domain.User;
import br.org.itinerario.infrastructure.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.security.Principal;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository users;
    private final MentorService mentors;

    public AdminController(UserRepository users, MentorService mentors) {
        this.users = users;
        this.mentors = mentors;
    }

    @PostMapping("/mentors/{id}/approve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void approve(Principal actor, @PathVariable UUID id) {
        mentors.review(actor.getName(), id, true);
    }

    @PostMapping("/mentors/{id}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reject(Principal actor, @PathVariable UUID id) {
        mentors.review(actor.getName(), id, false);
    }

    public record StatusInput(@NotNull User.Status status) {}

    @PutMapping("/users/{id}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void status(
        Principal actor,
        @PathVariable UUID id,
        @Valid @RequestBody StatusInput input
    ) {
        mentors.status(actor.getName(), id, input.status());
    }

    public record UserView(UUID id, String name, User.Role role, User.Status status) {}

    @GetMapping("/users")
    @Transactional(readOnly = true)
    public List<UserView> users(@RequestParam(defaultValue = "1") int page) {
        return users
            .findAll(MentorService.page(page))
            .stream()
            .map(user -> new UserView(user.id, user.name, user.role, user.status))
            .toList();
    }

    public record PendingMentor(
        UUID id,
        String name,
        String bio,
        String expertiseAreas,
        String experience
    ) {}

    @GetMapping("/mentors/pending")
    @Transactional(readOnly = true)
    public List<PendingMentor> pending(@RequestParam(defaultValue = "1") int page) {
        return users
            .findByMentorProfileApprovalStatus(
                User.Approval.PENDING_APPROVAL,
                PageRequest.of(
                    Math.max(1, Math.min(page, 10000)) - 1,
                    20,
                    Sort.by("createdAt").and(Sort.by("id"))
                )
            )
            .stream()
            .map(user ->
                new PendingMentor(
                    user.id,
                    user.name,
                    user.mentorProfile.bio,
                    user.mentorProfile.expertiseAreas,
                    user.mentorProfile.experience
                )
            )
            .toList();
    }
}
