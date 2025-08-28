package com.hairbook.entity;

import java.time.LocalDateTime;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "notifications")
@Schema(description = "Notification envoyée à un utilisateur")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identifiant unique de la notification")
    private Long id;

    @Schema(description = "Utilisateur qui reçoit la notification")
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Schema(description = "Type de notification (ex: SYSTEM, BOOKING, PAYMENT)")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Schema(description = "Message de la notification")
    @Column(nullable = false)
    private String message;

    @Schema(description = "Statut actuel de la notification (UNREAD, READ)")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status = NotificationStatus.UNREAD;

    @Schema(description = "Date et heure d'envoi de la notification")
    @Column(nullable = false, updatable = false)
    private LocalDateTime sentAt;

    @Schema(description = "Date et heure à laquelle la notification a été lue")
    @Column
    private LocalDateTime readAt;

    public Notification() {
    }

    // --- Hooks automatiques ---
    @PrePersist
    public void prePersist() {
        this.sentAt = LocalDateTime.now();
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

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

}
