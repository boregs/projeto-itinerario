package br.org.itinerario.infrastructure;

import br.org.itinerario.domain.*;
import java.util.UUID;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LearningTrackRepository extends JpaRepository<LearningTrack, UUID> {
    Page<LearningTrack> findByStatus(LearningStatus status, Pageable page);
}
