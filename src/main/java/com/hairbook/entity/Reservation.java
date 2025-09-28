package com.hairbook.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Index;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations", indexes = {
        @Index(name = "idx_reservation_user", columnList = "user_id"),
        @Index(name = "idx_reservation_start", columnList = "startTime")
})
@Schema(description = "Réservation d’un service au salon")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identifiant unique de la réservation", example = "101", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Client qui effectue la réservation")
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Schema(description = "Service réservé par le client")
    @ManyToOne(optional = false)
    @JoinColumn(name = "service_item_id")
    private ServiceItem serviceItem;

    @Schema(description = "Date/heure de début du rendez-vous")
    @Column(nullable = false)
    private LocalDateTime startTime;

    @Schema(description = "Statut actuel de la réservation (PENDING, CONFIRMED, CANCELED)")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.PENDING;

    @Schema(description = "Notes du client ou du coiffeur")
    @Column(length = 1000)
    private String notes;

    @Schema(description = "Date/heure de création de la réservation")
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Schema(description = "Date/heure de dernière modification")
    private LocalDateTime updatedAt;

    public Reservation() {
    }

    // --- Hooks automatiques pour les dates ---
    @jakarta.persistence.PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null)
            this.status = ReservationStatus.PENDING;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
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

    public ServiceItem getServiceItem() {
        return serviceItem;
    }

    public void setServiceItem(ServiceItem serviceItem) {
        this.serviceItem = serviceItem;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Alias pour compatibilité avec les mappers

    public LocalDateTime getReservationDateTime() {
        return startTime;
    }

    public void setReservationDateTime(LocalDateTime dateTime) {
        this.startTime = dateTime;
    }
}
