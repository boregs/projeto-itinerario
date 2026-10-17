package br.org.itinerario.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "student_activity_attempt")
public class StudentActivityAttempt {

    @Id
    public UUID id = UUID.randomUUID();

    @Column(nullable = false)
    public UUID studentId;

    @Column(nullable = false)
    public UUID activityId;

    @Column(nullable = false)
    public int answer;

    @Column(nullable = false)
    public boolean isCorrect;

    @Column(nullable = false)
    public int score;

    @Column(nullable = false)
    public Instant createdAt = Instant.now();
}
