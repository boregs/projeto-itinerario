package br.org.itinerario.infrastructure;

import br.org.itinerario.domain.*;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearningModuleRepository extends JpaRepository<LearningModule, UUID> {
    List<LearningModule> findByTrackIdOrderByPositionAscIdAsc(UUID trackId);
}
