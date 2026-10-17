package br.org.itinerario.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "app_user")
public class User {

    public enum Role {
        STUDENT,
        MENTOR,
        ADMIN,
    }

    public enum Status {
        ACTIVE,
        BLOCKED,
    }

    public enum Approval {
        PENDING_APPROVAL,
        APPROVED,
        REJECTED,
    }

    @Id
    public UUID id = UUID.randomUUID();

    @Column(nullable = false, length = 100)
    public String name;

    @Column(nullable = false, unique = true, length = 254)
    public String email;

    @Column(nullable = false)
    public String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public Status status = Status.ACTIVE;

    @Column(nullable = false)
    public Instant createdAt = Instant.now();

    @Column(nullable = false)
    public Instant updatedAt = Instant.now();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    public StudentProfile studentProfile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    public MentorProfile mentorProfile;
}
