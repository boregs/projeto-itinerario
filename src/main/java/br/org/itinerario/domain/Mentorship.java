package br.org.itinerario.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "mentorship")
public class Mentorship {

    public enum Status {
        MATCHED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
    }

    @Id
    public UUID id = UUID.randomUUID();

    @Column(nullable = false, unique = true)
    public UUID requestId;

    @Column(nullable = false)
    public UUID studentId;

    @Column(nullable = false)
    public UUID mentorId;

    @Column(nullable = false)
    public Instant scheduledAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public Status status = Status.MATCHED;

    @Column(length = 2000)
    public String summary;

    @Column(nullable = false)
    public Instant createdAt = Instant.now();

    public Instant completedAt;
}
