package com.hairbook.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Demande de paiement (Stripe test)")
public class PaymentRequestDTO {

    @Schema(description = "Identifiant de la réservation à régler")
    @NotNull
    private Long reservationId;

    @Schema(description = "Montant à payer")
    @NotNull
    @Positive
    private BigDecimal amount;

    @Schema(description = "Devise ISO")
    @NotNull
    private String currency;

    @Schema(description = "Identifiant de méthode de paiement (fake/test)")
    private String paymentMethodId;

    public PaymentRequestDTO() {
    }

    public PaymentRequestDTO(Long reservationId, BigDecimal amount, String currency, String paymentMethodId) {
        this.reservationId = reservationId;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethodId = paymentMethodId;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }
}
