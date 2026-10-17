package br.org.itinerario.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "student_activity_completion")
public class StudentActivityCompletion {

    @Id
    public UUID id = UUID.randomUUID();

    @Column(nullable = false)
    public UUID studentId;

    @Column(nullable = false)
    public UUID activityId;

    @Column(nullable = false)
    public int points;

    @Column(nullable = false)
    public Instant completedAt = Instant.now();
}
