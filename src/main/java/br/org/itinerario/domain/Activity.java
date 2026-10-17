package br.org.itinerario.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "activity")
public class Activity {

    public enum Type {
        MULTIPLE_CHOICE,
        TRUE_FALSE,
        LOGIC_CHALLENGE,
    }

    @Id
    public UUID id = UUID.randomUUID();

    @Column(nullable = false)
    public UUID moduleId;

    @Column(nullable = false, length = 150)
    public String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    public Type type;

    @Column(nullable = false, length = 2000)
    public String question;

    @Column(nullable = false, length = 5000)
    public String optionsJson;

    @Column(nullable = false)
    public int correctAnswer;

    @Column(nullable = false, length = 2000)
    public String explanation;

    @Column(nullable = false)
    public int points;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public LearningStatus status = LearningStatus.ACTIVE;

    @Column(nullable = false)
    public int position;
}
