package br.org.itinerario.application;

import br.org.itinerario.domain.*;
import br.org.itinerario.infrastructure.*;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MentorService {

    private final UserRepository users;
    private final AuditRepository audit;
    private final AccountAccess access;
    private final MentorshipRepository mentorships;

    public MentorService(
        UserRepository users,
        AuditRepository audit,
        AccountAccess access,
        MentorshipRepository mentorships
    ) {
        this.users = users;
        this.audit = audit;
        this.access = access;
        this.mentorships = mentorships;
    }

    public record ProfileInput(
        @SafeText @NotBlank @Size(max = 100) String name,
        @SafeText @NotBlank @Size(max = 1000) String bio,
        @SafeText @NotBlank @Size(max = 500) String expertiseAreas,
        @SafeText @NotBlank @Size(max = 500) String availability,
        @NotBlank @Size(max = 1000) String experience
    ) {}

    public record Profile(
        UUID id,
        String name,
        String bio,
        String expertiseAreas,
        String availability,
        User.Approval approvalStatus,
        long completedMentorships
    ) {}

    private Profile view(User user) {
        var profile = user.mentorProfile;
        return new Profile(
            user.id,
            SafeTextValidator.display(user.name),
            SafeTextValidator.display(profile.bio),
            SafeTextValidator.display(profile.expertiseAreas),
            SafeTextValidator.display(profile.availability),
            profile.approvalStatus,
            mentorships.countByMentorIdAndStatus(user.id, Mentorship.Status.COMPLETED)
        );
    }

    public static Pageable page(int page) {
        return PageRequest.of(
            Math.max(1, Math.min(page, 10000)) - 1,
            20,
            Sort.by("createdAt").and(Sort.by("id"))
        );
    }

    @Transactional(readOnly = true)
    public List<Profile> list(int page) {
        return users
            .findByRoleAndStatusAndMentorProfileApprovalStatus(
                User.Role.MENTOR,
                User.Status.ACTIVE,
                User.Approval.APPROVED,
                page(page)
            )
            .stream()
            .map(this::view)
            .toList();
    }

    @Transactional(readOnly = true)
    public Profile detail(UUID id) {
        var user = users.findById(id).orElseThrow(BusinessException::notFound);
        if (
            user.role != User.Role.MENTOR ||
            user.status != User.Status.ACTIVE ||
            user.mentorProfile.approvalStatus != User.Approval.APPROVED
        ) throw BusinessException.notFound();
        return view(user);
    }

    @Transactional(readOnly = true)
    public Profile own(String actor) {
        return view(access.role(actor, User.Role.MENTOR));
    }

    public Profile update(String actor, ProfileInput input) {
        var user = access.lockRole(actor, User.Role.MENTOR);
        user.name = input.name().strip();
        var profile = user.mentorProfile;
        profile.bio = input.bio().strip();
        profile.expertiseAreas = input.expertiseAreas().strip();
        profile.availability = input.availability().strip();
        profile.experience = input.experience().strip();
        // Changed public credentials must pass administrative review again.
        profile.approvalStatus = User.Approval.PENDING_APPROVAL;
        profile.approvedAt = null;
        profile.approvedBy = null;
        user.updatedAt = Instant.now();
        audit.save(new AuditEvent(user.id, user.id, "MENTOR_PROFILE_UPDATED"));
        return view(user);
    }

    public void review(String actor, UUID id, boolean approve) {
        var admin = access.role(actor, User.Role.ADMIN);
        var user = users.lockById(id).orElseThrow(BusinessException::notFound);
        if (
            user.role != User.Role.MENTOR || user.mentorProfile == null
        ) throw BusinessException.notFound();
        if (user.status != User.Status.ACTIVE) throw BusinessException.conflict(
            "Mentor bloqueado não pode ser avaliado."
        );
        var profile = user.mentorProfile;
        if (
            profile.approvalStatus != User.Approval.PENDING_APPROVAL
        ) throw BusinessException.conflict("Este perfil já foi avaliado.");
        profile.approvalStatus = approve ? User.Approval.APPROVED : User.Approval.REJECTED;
        profile.approvedAt = approve ? Instant.now() : null;
        profile.approvedBy = approve ? admin.id : null;
        user.updatedAt = Instant.now();
        audit.save(
            new AuditEvent(admin.id, user.id, approve ? "MENTOR_APPROVED" : "MENTOR_REJECTED")
        );
    }

    public void status(String actor, UUID id, User.Status status) {
        var admin = access.role(actor, User.Role.ADMIN);
        var user = users.lockById(id).orElseThrow(BusinessException::notFound);
        if (user.role == User.Role.ADMIN) throw BusinessException.conflict(
            "Alteração de administradores indisponível nesta operação."
        );
        if (user.status == status) return;
        user.status = status;
        user.updatedAt = Instant.now();
        audit.save(new AuditEvent(admin.id, user.id, "USER_" + status.name()));
    }
}
