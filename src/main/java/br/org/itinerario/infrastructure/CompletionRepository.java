package br.org.itinerario.infrastructure;

import br.org.itinerario.domain.StudentActivityCompletion;
import java.util.*;
import org.springframework.data.jpa.repository.*;

public interface CompletionRepository extends JpaRepository<StudentActivityCompletion, UUID> {
    boolean existsByStudentIdAndActivityId(UUID studentId, UUID activityId);
    List<StudentActivityCompletion> findByStudentId(UUID studentId);

    @Query(
        "select coalesce(sum(c.points), 0) from StudentActivityCompletion c where c.studentId = :studentId"
    )
    long points(UUID studentId);
}
