package br.org.itinerario.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "learning_track")
public class LearningTrack {

    @Id
    public UUID id = UUID.randomUUID();

    @Column(nullable = false, length = 150)
    public String title;

    @Column(nullable = false, length = 1000)
    public String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public LearningStatus status = LearningStatus.ACTIVE;

    @Column(nullable = false)
    public int position;
}
