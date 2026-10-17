package br.org.itinerario.infrastructure;

import br.org.itinerario.domain.StudentActivityAttempt;
import java.util.UUID;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttemptRepository extends JpaRepository<StudentActivityAttempt, UUID> {
    boolean existsByActivityId(UUID activityId);
    long countByStudentIdAndActivityId(UUID studentId, UUID activityId);
    Page<StudentActivityAttempt> findByStudentIdOrderByCreatedAtDescIdAsc(
        UUID studentId,
        Pageable page
    );
}
