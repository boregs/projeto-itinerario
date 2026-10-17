package br.org.itinerario.api;

import br.org.itinerario.application.MentorService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class MentorController {

    private final MentorService mentors;

    public MentorController(MentorService mentors) {
        this.mentors = mentors;
    }

    @GetMapping("/api/mentors")
    public List<MentorService.Profile> list(@RequestParam(defaultValue = "1") int page) {
        return mentors.list(page);
    }

    @GetMapping("/api/mentors/{id}")
    public MentorService.Profile detail(@PathVariable UUID id) {
        return mentors.detail(id);
    }

    @GetMapping("/api/mentor/profile")
    public MentorService.Profile own(Principal actor) {
        return mentors.own(actor.getName());
    }

    @PutMapping("/api/mentor/profile")
    public MentorService.Profile update(
        Principal actor,
        @Valid @RequestBody MentorService.ProfileInput input
    ) {
        return mentors.update(actor.getName(), input);
    }
}
