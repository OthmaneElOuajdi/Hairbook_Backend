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
@Table(name = "blocked_slots", indexes = {
        @Index(name = "idx_blockedslot_range", columnList = "startAt,endAt")
})
@Schema(description = "Créneaux bloqués (maintenance, congés, événement interne, etc.)")
public class BlockedSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identifiant unique du créneau bloqué")
    private Long id;

    @Schema(description = "Date/heure de début du créneau bloqué")
    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Schema(description = "Date/heure de fin du créneau bloqué")
    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Schema(description = "Raison du blocage (ex: Congés annuels, Maintenance)")
    @Column(length = 255)
    private String reason;

    @Schema(description = "Administrateur ayant créé le blocage")
    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User createdBy;

    @Schema(description = "Date/heure de création du blocage")
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public BlockedSlot() {
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

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public void setEndAt(LocalDateTime endAt) {
        this.endAt = endAt;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
