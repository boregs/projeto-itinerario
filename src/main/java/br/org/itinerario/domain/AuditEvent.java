package br.org.itinerario.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_event")
public class AuditEvent {

    @Id
    public UUID id = UUID.randomUUID();

    public UUID actorId;

    @Column(nullable = false)
    public UUID targetId;

    @Column(nullable = false, length = 60)
    public String action;

    @Column(nullable = false)
    public Instant createdAt = Instant.now();

    protected AuditEvent() {}

    public AuditEvent(UUID actorId, UUID targetId, String action) {
        this.actorId = actorId;
        this.targetId = targetId;
        this.action = action;
    }
}
