package br.org.itinerario.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "learning_module")
public class LearningModule {

    @Id
    public UUID id = UUID.randomUUID();

    @Column(nullable = false)
    public UUID trackId;

    @Column(nullable = false, length = 150)
    public String title;

    @Column(nullable = false, length = 10000)
    public String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public LearningStatus status = LearningStatus.ACTIVE;

    @Column(nullable = false)
    public int position;
}
