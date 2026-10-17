package br.org.itinerario.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "student_achievement")
public class StudentAchievement {

    public enum Code {
        FIRST_ACTIVITY,
        FIRST_TRACK,
    }

    @Id
    public UUID id = UUID.randomUUID();

    @Column(nullable = false)
    public UUID studentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    public Code code;

    @Column(nullable = false)
    public Instant achievedAt = Instant.now();
}
