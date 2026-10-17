package br.org.itinerario.application;

import br.org.itinerario.domain.*;
import jakarta.validation.constraints.*;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

    public record StudentInput(
        @SafeText @NotBlank @Size(max = 100) String name,
        @NotBlank @Email @Size(max = 254) String email,
        @NotNull @Size(min = 12, max = 128) String password,
        @NotNull @Min(1) @Max(3) Integer schoolYear,
        @SafeText @NotBlank @Size(max = 500) String interests
    ) {}

    public record MentorInput(
        @SafeText @NotBlank @Size(max = 100) String name,
        @NotBlank @Email @Size(max = 254) String email,
        @NotNull @Size(min = 12, max = 128) String password,
        @SafeText @NotBlank @Size(max = 1000) String bio,
        @SafeText @NotBlank @Size(max = 500) String expertiseAreas,
        @SafeText @NotBlank @Size(max = 500) String availability,
        @NotBlank @Size(max = 1000) String experience
    ) {}

    private final UserStore users;
    private final PasswordEncoder passwords;

    public RegistrationService(UserStore users, PasswordEncoder passwords) {
        this.users = users;
        this.passwords = passwords;
    }

    public static String normalizeEmail(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }

    private User create(String name, String email, String password, User.Role role) {
        var user = new User();
        user.name = name.strip();
        user.email = normalizeEmail(email);
        user.passwordHash = passwords.encode(password);
        user.role = role;
        return user;
    }

    @Transactional
    public User register(StudentInput input) {
        var user = create(input.name(), input.email(), input.password(), User.Role.STUDENT);
        var profile = new StudentProfile();
        profile.user = user;
        profile.schoolYear = input.schoolYear();
        profile.interests = input.interests().strip();
        user.studentProfile = profile;
        return users.save(user);
    }

    @Transactional
    public User register(MentorInput input) {
        var user = create(input.name(), input.email(), input.password(), User.Role.MENTOR);
        var profile = new MentorProfile();
        profile.user = user;
        profile.bio = input.bio().strip();
        profile.expertiseAreas = input.expertiseAreas().strip();
        profile.availability = input.availability().strip();
        profile.experience = input.experience().strip();
        user.mentorProfile = profile;
        return users.save(user);
    }
}
