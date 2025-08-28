package com.hairbook.entity;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_entity", columnList = "entity_name,entity_id"),
        @Index(name = "idx_audit_createdAt", columnList = "created_at")
})
@Schema(description = "Historique des actions (sécurité, administration, système)")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identifiant unique du log")
    private Long id;

    @Schema(description = "Utilisateur à l'origine de l'action (null si action système)")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Schema(description = "Nom de l'entité affectée (ex: 'Reservation', 'User')")
    @Column(name = "entity_name", nullable = false, length = 120)
    private String entityName;

    @Schema(description = "Identifiant de l'entité affectée")
    @Column(name = "entity_id")
    private Long entityId;

    @Schema(description = "Action réalisée (CREATE, UPDATE, DELETE, LOGIN, LOGOUT, etc.)")
    @Column(nullable = false, length = 60)
    private String action;

    @Schema(description = "Détails supplémentaires (JSON ou texte libre)")
    @Column(columnDefinition = "text")
    private String details;

    @Schema(description = "Date/heure de l'action")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public AuditLog() {
    }

    // --- Hooks automatiques ---
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
