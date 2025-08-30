package com.hairbook.dto.loyalty;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Points de fidélité d'un utilisateur")
public class LoyaltyPointDTO {

    @Schema(description = "Identifiant de l'utilisateur")
    private Long userId;

    @Schema(description = "Variation de points (peut être négatif)")
    private int deltaPoints;

    @Schema(description = "Total actuel de points")
    private int totalPoints;

    @Schema(description = "Raison de l'ajout/retrait")
    private String reason;

    @Schema(description = "Date de l'opération")
    private LocalDateTime createdAt;

    public LoyaltyPointDTO() {
    }

    public LoyaltyPointDTO(Long userId, int deltaPoints, int totalPoints, String reason, LocalDateTime createdAt) {
        this.userId = userId;
        this.deltaPoints = deltaPoints;
        this.totalPoints = totalPoints;
        this.reason = reason;
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getDeltaPoints() {
        return deltaPoints;
    }

    public void setDeltaPoints(int deltaPoints) {
        this.deltaPoints = deltaPoints;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
