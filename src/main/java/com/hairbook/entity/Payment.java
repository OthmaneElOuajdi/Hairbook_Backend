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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "payments")
@Schema(description = "Représente un paiement effectué par un utilisateur")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identifiant unique du paiement")
    private Long id;

    @Schema(description = "Utilisateur ayant effectué le paiement")
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Schema(description = "Réservation associée au paiement")
    @OneToOne(optional = false)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Schema(description = "Montant du paiement en euros")
    @Column(nullable = false)
    private double amount;

    @Schema(description = "Statut du paiement (PENDING, COMPLETED, FAILED)")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Schema(description = "Message lié au paiement (erreur, confirmation, etc.)")
    @Column(length = 500)
    private String message;

    @Schema(description = "Date et heure du paiement")
    @Column(nullable = false, updatable = false)
    private LocalDateTime paymentDate;

    @Schema(description = "Méthode de paiement utilisée (ex: Carte, PayPal, Bancontact)")
    @Column(length = 50)
    private String paymentMethod;

    @Schema(description = "Identifiant de transaction externe (fourni par le prestataire de paiement)")
    @Column(length = 100)
    private String transactionId;

    @Schema(description = "Date de création de l'enregistrement")
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Schema(description = "Date de dernière modification de l'enregistrement")
    private LocalDateTime updatedAt;

    public Payment() {
    }

    @PrePersist
    public void prePersist() {
        this.paymentDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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

}
