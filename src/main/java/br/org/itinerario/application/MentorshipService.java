package br.org.itinerario.application;

import br.org.itinerario.domain.*;
import br.org.itinerario.infrastructure.*;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MentorshipService {

    private final MentorshipRequestRepository requests;
    private final MentorshipRepository mentorships;
    private final UserRepository users;
    private final AuditRepository audit;
    private final AccountAccess access;

    public MentorshipService(
        MentorshipRequestRepository requests,
        MentorshipRepository mentorships,
        UserRepository users,
        AuditRepository audit,
        AccountAccess access
    ) {
        this.requests = requests;
        this.mentorships = mentorships;
        this.users = users;
        this.audit = audit;
        this.access = access;
    }

    public record RequestInput(
        @SafeText @NotBlank @Size(max = 150) String subject,
        @SafeText @NotBlank @Size(max = 100) String area,
        @SafeText @NotBlank @Size(max = 1000) String description,
        @SafeText @NotBlank @Size(max = 500) String objective,
        @SafeText @NotBlank @Size(max = 100) String preferredSchedule
    ) {}

    public record AcceptInput(@NotNull @Future Instant scheduledAt) {}

    public record StatusInput(
        @NotNull Mentorship.Status status,
        @SafeText @Size(max = 2000) String summary
    ) {}

    public record RequestView(
        UUID id,
        String subject,
        String area,
        String description,
        String objective,
        String preferredSchedule,
        MentorshipRequest.Status status,
        Instant createdAt
    ) {
        static RequestView from(MentorshipRequest request) {
            return new RequestView(
                request.id,
                SafeTextValidator.display(request.subject),
                SafeTextValidator.display(request.area),
                SafeTextValidator.display(request.description),
                SafeTextValidator.display(request.objective),
                SafeTextValidator.display(request.preferredSchedule),
                request.status,
                request.createdAt
            );
        }
    }

    public record SessionView(
        UUID id,
        UUID requestId,
        String subject,
        Instant scheduledAt,
        Mentorship.Status status,
        String summary,
        Instant createdAt,
        Instant completedAt
    ) {}

    private SessionView view(Mentorship session) {
        var request = requests.findById(session.requestId).orElseThrow(BusinessException::notFound);
        return new SessionView(
            session.id,
            session.requestId,
            SafeTextValidator.display(request.subject),
            session.scheduledAt,
            session.status,
            SafeTextValidator.display(session.summary),
            session.createdAt,
            session.completedAt
        );
    }

    public RequestView create(String actor, RequestInput input) {
        var student = access.lockRole(actor, User.Role.STUDENT);
        var request = new MentorshipRequest();
        request.studentId = student.id;
        request.subject = input.subject().strip();
        request.area = Matching.normalize(input.area());
        request.description = input.description().strip();
        request.objective = input.objective().strip();
        request.preferredSchedule = Matching.normalize(input.preferredSchedule());
        requests.save(request);
        audit.save(new AuditEvent(student.id, request.id, "REQUEST_CREATED"));
        return RequestView.from(request);
    }

    @Transactional(readOnly = true)
    public List<RequestView> ownRequests(String actor, int page) {
        var student = access.role(actor, User.Role.STUDENT);
        return requests
            .findByStudentId(student.id, MentorService.page(page))
            .map(RequestView::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<RequestView> available(String actor, int page) {
        var mentor = access.approvedMentor(actor);
        var areas = Matching.tokens(mentor.mentorProfile.expertiseAreas);
        var schedules = Matching.tokens(mentor.mentorProfile.availability);
        if (areas.isEmpty() || schedules.isEmpty()) return List.of();
        return requests
            .available(
                MentorshipRequest.Status.OPEN,
                areas,
                schedules,
                User.Status.ACTIVE,
                MentorService.page(page)
            )
            .map(RequestView::from)
            .toList();
    }

    public SessionView accept(String actor, UUID requestId, AcceptInput input) {
        var mentor = access.lockApprovedMentor(actor);
        var request = requests.lockById(requestId).orElseThrow(BusinessException::notFound);
        if (request.status != MentorshipRequest.Status.OPEN) throw BusinessException.conflict(
            "Solicitação indisponível para aceite."
        );
        var student = users.lockById(request.studentId).orElseThrow(BusinessException::notFound);
        if (student.status != User.Status.ACTIVE) throw BusinessException.conflict(
            "Solicitação indisponível para aceite."
        );
        if (
            !Matching.compatible(
                mentor.mentorProfile.expertiseAreas,
                mentor.mentorProfile.availability,
                request.area,
                request.preferredSchedule
            )
        ) throw BusinessException.forbidden();
        if (
            input.scheduledAt() == null || !input.scheduledAt().isAfter(Instant.now())
        ) throw new BusinessException(400, "Escolha uma data futura para a sessão.");
        var session = new Mentorship();
        session.requestId = request.id;
        session.studentId = student.id;
        session.mentorId = mentor.id;
        session.scheduledAt = input.scheduledAt();
        mentorships.saveAndFlush(session);
        request.status = MentorshipRequest.Status.MATCHED;
        audit.save(new AuditEvent(mentor.id, session.id, "MENTORSHIP_ACCEPTED"));
        return view(session);
    }

    public void cancelRequest(String actor, UUID id) {
        var student = access.role(actor, User.Role.STUDENT);
        var request = requests.lockById(id).orElseThrow(BusinessException::notFound);
        if (!request.studentId.equals(student.id)) throw BusinessException.notFound();
        if (request.status != MentorshipRequest.Status.OPEN) throw BusinessException.conflict(
            "Somente solicitações abertas podem ser canceladas aqui."
        );
        request.status = MentorshipRequest.Status.CANCELLED;
        audit.save(new AuditEvent(student.id, id, "REQUEST_CANCELLED"));
    }

    @Transactional(readOnly = true)
    public List<SessionView> ownSessions(String actor, int page) {
        var user = access.active(actor);
        return switch (user.role) {
            case STUDENT -> mentorships
                .findByStudentId(user.id, MentorService.page(page))
                .map(this::view)
                .toList();
            case MENTOR -> mentorships
                .findByMentorId(user.id, MentorService.page(page))
                .map(this::view)
                .toList();
            default -> throw BusinessException.forbidden();
        };
    }

    private void requireParticipant(User user, Mentorship session) {
        if (
            user.role != User.Role.ADMIN &&
            !session.studentId.equals(user.id) &&
            !session.mentorId.equals(user.id)
        ) throw BusinessException.notFound();
    }

    @Transactional(readOnly = true)
    public SessionView detail(String actor, UUID id) {
        var user = access.active(actor);
        var session = mentorships.findById(id).orElseThrow(BusinessException::notFound);
        requireParticipant(user, session);
        return view(session);
    }

    @Transactional(readOnly = true)
    public List<SessionView> all(String actor, int page) {
        access.role(actor, User.Role.ADMIN);
        return mentorships.findAll(MentorService.page(page)).map(this::view).toList();
    }

    public SessionView transition(String actor, UUID id, StatusInput input) {
        var user = access.active(actor);
        var session = mentorships.lockById(id).orElseThrow(BusinessException::notFound);
        requireParticipant(user, session);
        boolean cancel = input.status() == Mentorship.Status.CANCELLED;
        if (!cancel && user.role == User.Role.STUDENT) throw BusinessException.forbidden();
        if (!cancel && user.role == User.Role.MENTOR) access.approvedMentor(actor);
        boolean allowed = switch (session.status) {
            case MATCHED -> input.status() == Mentorship.Status.IN_PROGRESS || cancel;
            case IN_PROGRESS -> input.status() == Mentorship.Status.COMPLETED || cancel;
            default -> false;
        };
        if (!allowed) throw BusinessException.conflict("Transição de status não permitida.");
        if (
            user.role == User.Role.STUDENT && input.summary() != null
        ) throw BusinessException.forbidden();
        if (
            input.status() == Mentorship.Status.COMPLETED &&
            (input.summary() == null || input.summary().isBlank())
        ) throw new BusinessException(400, "Informe um resumo para concluir a mentoria.");
        session.status = input.status();
        if (input.summary() != null) session.summary = input.summary().strip();
        session.completedAt = session.status == Mentorship.Status.COMPLETED ? Instant.now() : null;
        var request = requests.lockById(session.requestId).orElseThrow(BusinessException::notFound);
        request.status = MentorshipRequest.Status.valueOf(session.status.name());
        audit.save(new AuditEvent(user.id, session.id, "MENTORSHIP_" + session.status.name()));
        return view(session);
    }
}
