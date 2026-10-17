package br.org.itinerario.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "student_profile")
public class StudentProfile {

    @Id
    public UUID userId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "user_id")
    public User user;

    @Column(nullable = false)
    public int schoolYear;

    @Column(nullable = false, length = 500)
    public String interests;
}
