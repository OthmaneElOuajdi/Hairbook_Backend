package com.hairbook.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Réponse après tentative de paiement")
public class PaymentResponseDTO {

    @Schema(description = "Identifiant du paiement")
    private Long id;

    @Schema(description = "Montant du paiement")
    private double amount;

    @Schema(description = "Statut du paiement")
    private String status;

    @Schema(description = "ID de transaction")
    private String transactionId;

    @Schema(description = "Message ou erreur éventuelle")
    private String message;

    @Schema(description = "ID de l'utilisateur")
    private Long userId;

    @Schema(description = "Nom de l'utilisateur")
    private String userName;

    @Schema(description = "ID de la réservation")
    private Long reservationId;

    @Schema(description = "Date/heure de la réservation")
    private LocalDateTime reservationDateTime;

    @Schema(description = "Horodatage de création")
    private LocalDateTime createdAt;

    @Schema(description = "Horodatage de modification")
    private LocalDateTime updatedAt;

    @Schema(description = "Méthode de paiement utilisée")
    private String paymentMethod;

    public PaymentResponseDTO() {
    }

    public PaymentResponseDTO(Long id, String status, String message, LocalDateTime createdAt) {
        this.id = id;
        this.status = status;
        this.message = message;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public LocalDateTime getReservationDateTime() {
        return reservationDateTime;
    }

    public void setReservationDateTime(LocalDateTime reservationDateTime) {
        this.reservationDateTime = reservationDateTime;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
