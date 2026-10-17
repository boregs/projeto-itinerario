package br.org.itinerario.api;

import br.org.itinerario.application.*;
import br.org.itinerario.domain.*;
import br.org.itinerario.infrastructure.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.security.Principal;
import java.time.Instant;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/profile")
public class StudentProfileController {

    private final AccountAccess access;
    private final UserRepository users;

    public StudentProfileController(AccountAccess access, UserRepository users) {
        this.access = access;
        this.users = users;
    }

    public record Input(
        @SafeText @NotBlank @Size(max = 100) String name,
        @NotNull @Min(1) @Max(3) Integer schoolYear,
        @SafeText @NotBlank @Size(max = 500) String interests
    ) {}

    public record Profile(String name, int schoolYear, String interests) {}

    private Profile view(User user) {
        return new Profile(
            user.name,
            user.studentProfile.schoolYear,
            user.studentProfile.interests
        );
    }

    @GetMapping
    @Transactional(readOnly = true)
    public Profile get(Principal actor) {
        return view(access.role(actor.getName(), User.Role.STUDENT));
    }

    @PutMapping
    @Transactional
    public Profile update(Principal actor, @Valid @RequestBody Input input) {
        var user = access.lockRole(actor.getName(), User.Role.STUDENT);
        user.name = input.name().strip();
        user.studentProfile.schoolYear = input.schoolYear();
        user.studentProfile.interests = input.interests().strip();
        user.updatedAt = Instant.now();
        return view(user);
    }
}
