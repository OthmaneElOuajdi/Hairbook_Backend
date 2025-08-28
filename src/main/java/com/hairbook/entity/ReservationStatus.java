package com.hairbook.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Statut d'une réservation")
public enum ReservationStatus {

    @Schema(description = "Réservation en attente de confirmation")
    PENDING,

    @Schema(description = "Réservation confirmée")
    CONFIRMED,

    @Schema(description = "Réservation annulée")
    CANCELLED,

    @Schema(description = "Réservation terminée")
    COMPLETED
}
