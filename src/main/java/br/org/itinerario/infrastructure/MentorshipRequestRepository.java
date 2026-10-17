package br.org.itinerario.infrastructure;

import br.org.itinerario.domain.MentorshipRequest;
import jakarta.persistence.LockModeType;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;

public interface MentorshipRequestRepository extends JpaRepository<MentorshipRequest, UUID> {
    Page<MentorshipRequest> findByStudentId(UUID studentId, Pageable pageable);

    @Query(
        "select r from MentorshipRequest r where r.status = :status and r.area in :areas and r.preferredSchedule in :schedules " +
            "and exists (select u.id from User u where u.id = r.studentId and u.status = :studentStatus)"
    )
    Page<MentorshipRequest> available(
        MentorshipRequest.Status status,
        Collection<String> areas,
        Collection<String> schedules,
        br.org.itinerario.domain.User.Status studentStatus,
        Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from MentorshipRequest r where r.id = :id")
    Optional<MentorshipRequest> lockById(UUID id);
}
