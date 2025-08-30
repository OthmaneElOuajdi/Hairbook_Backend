package com.hairbook.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Données pour créer un nouveau paiement")
public class PaymentCreateDTO {

    @Schema(description = "Montant du paiement")
    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private Double amount;

    @Schema(description = "Méthode de paiement")
    @NotNull(message = "La méthode de paiement est obligatoire")
    private String paymentMethod;

    @Schema(description = "ID de la réservation")
    @NotNull(message = "L'ID de la réservation est obligatoire")
    private Long reservationId;

    public PaymentCreateDTO() {
    }

    public PaymentCreateDTO(Double amount, String paymentMethod, Long reservationId) {
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.reservationId = reservationId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }
}
