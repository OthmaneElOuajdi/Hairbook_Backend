package com.hairbook.dto.notification;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Notification envoyée à un utilisateur")
public class NotificationDTO {

    @Schema(description = "Identifiant de la notification")
    private Long id;

    @Schema(description = "Identifiant de l'utilisateur")
    private Long userId;

    @Schema(description = "Titre")
    private String title;

    @Schema(description = "Message de la notification")
    private String message;

    @Schema(description = "Type")
    private String type;

    @Schema(description = "Statut")
    private String status;

    @Schema(description = "Date de création")
    private LocalDateTime createdAt;

    @Schema(description = "Date de lecture")
    private LocalDateTime readAt;

    public NotificationDTO() {
    }

    public NotificationDTO(Long id, Long userId, String title, String message, String type, String status,
            LocalDateTime createdAt, LocalDateTime readAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
        this.readAt = readAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
}
