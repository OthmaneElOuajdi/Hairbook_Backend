package com.hairbook.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Type de notification")
public enum NotificationType {

    @Schema(description = "Notification pour confirmation de réservation")
    BOOKING_CONFIRMATION,

    @Schema(description = "Notification pour annulation de réservation")
    BOOKING_CANCELLATION,

    @Schema(description = "Notification pour un rappel de rendez-vous")
    BOOKING_REMINDER,

    @Schema(description = "Notification concernant un paiement")
    PAYMENT_STATUS
}
