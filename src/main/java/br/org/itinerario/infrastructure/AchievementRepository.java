package br.org.itinerario.infrastructure;

import br.org.itinerario.domain.StudentAchievement;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementRepository extends JpaRepository<StudentAchievement, UUID> {
    boolean existsByStudentIdAndCode(UUID studentId, StudentAchievement.Code code);
    List<StudentAchievement> findByStudentIdOrderByAchievedAtAsc(UUID studentId);
}
