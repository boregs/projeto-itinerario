package br.org.itinerario.infrastructure;

import br.org.itinerario.domain.Mentorship;
import jakarta.persistence.LockModeType;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;

public interface MentorshipRepository extends JpaRepository<Mentorship, UUID> {
    Page<Mentorship> findByStudentId(UUID id, Pageable pageable);
    Page<Mentorship> findByMentorId(UUID id, Pageable pageable);
    long countByMentorIdAndStatus(UUID id, Mentorship.Status status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Mentorship m where m.id = :id")
    Optional<Mentorship> lockById(UUID id);
}
