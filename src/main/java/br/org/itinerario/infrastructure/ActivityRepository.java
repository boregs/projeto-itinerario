package br.org.itinerario.infrastructure;

import br.org.itinerario.domain.Activity;
import jakarta.persistence.LockModeType;
import java.util.*;
import org.springframework.data.jpa.repository.*;

public interface ActivityRepository extends JpaRepository<Activity, UUID> {
    List<Activity> findByModuleIdOrderByPositionAscIdAsc(UUID moduleId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Activity a where a.id = :id")
    Optional<Activity> lockById(UUID id);
}
