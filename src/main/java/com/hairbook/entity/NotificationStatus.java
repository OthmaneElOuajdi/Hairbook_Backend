package com.hairbook.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Statut d'une notification")
public enum NotificationStatus {

    @Schema(description = "Notification non lue")
    UNREAD,

    @Schema(description = "Notification lue")
    READ
}
