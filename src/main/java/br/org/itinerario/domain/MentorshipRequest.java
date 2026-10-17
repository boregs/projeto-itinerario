package br.org.itinerario.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "mentorship_request")
public class MentorshipRequest {

    public enum Status {
        OPEN,
        MATCHED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
    }

    @Id
    public UUID id = UUID.randomUUID();

    @Column(nullable = false)
    public UUID studentId;

    @Column(nullable = false, length = 150)
    public String subject;

    @Column(nullable = false, length = 100)
    public String area;

    @Column(nullable = false, length = 1000)
    public String description;

    @Column(nullable = false, length = 500)
    public String objective;

    @Column(nullable = false, length = 100)
    public String preferredSchedule;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public Status status = Status.OPEN;

    @Column(nullable = false)
    public Instant createdAt = Instant.now();
}
