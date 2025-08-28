package com.hairbook.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Statut d'un paiement")
public enum PaymentStatus {

    @Schema(description = "Paiement en attente")
    PENDING,

    @Schema(description = "Paiement réussi")
    COMPLETED,

    @Schema(description = "Paiement échoué")
    FAILED,

    @Schema(description = "Paiement remboursé")
    REFUNDED
}
