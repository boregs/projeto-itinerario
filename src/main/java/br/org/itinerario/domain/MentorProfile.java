package br.org.itinerario.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "mentor_profile")
public class MentorProfile {

    @Id
    public UUID userId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "user_id")
    public User user;

    @Column(nullable = false, length = 1000)
    public String bio;

    @Column(nullable = false, length = 500)
    public String expertiseAreas;

    @Column(nullable = false, length = 500)
    public String availability;

    @Column(nullable = false, length = 1000)
    public String experience;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public User.Approval approvalStatus = User.Approval.PENDING_APPROVAL;

    public Instant approvedAt;
    public UUID approvedBy;
}
