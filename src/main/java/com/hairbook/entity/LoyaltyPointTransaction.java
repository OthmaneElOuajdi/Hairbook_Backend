package com.hairbook.entity;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Index;


@Entity
@Table(name = "loyalty_point_transactions", indexes = {
        @Index(name = "idx_loyalty_tx_user", columnList = "user_id"),
        @Index(name = "idx_loyalty_tx_date", columnList = "transaction_date")
})
@Schema(description = "Historique des transactions de points de fidélité")
public class LoyaltyPointTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identifiant unique de la transaction de points")
    private Long id;

    @Schema(description = "Utilisateur concerné par la transaction")
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Schema(description = "Nombre de points gagnés (positif) ou dépensés (négatif)")
    @Column(nullable = false)
    private int points;

    @Schema(description = "Description libre de la transaction")
    private String description;

    @Schema(description = "Date/heure de la transaction")
    @Column(name = "transaction_date", nullable = false, updatable = false)
    private LocalDateTime transactionDate;

    public LoyaltyPointTransaction() {
    }

    @PrePersist
    public void prePersist() {
        this.transactionDate = LocalDateTime.now();
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

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
}
