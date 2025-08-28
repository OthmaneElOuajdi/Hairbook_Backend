package com.hairbook.entity;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "idx_refreshtoken_token", columnList = "token", unique = true)
})
@Schema(description = "Token d'actualisation pour l'authentification JWT")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identifiant unique du refresh token")
    private Long id;

    @Schema(description = "Utilisateur propriétaire du refresh token")
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Schema(description = "Valeur opaque du token (longue et aléatoire)")
    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @Schema(description = "Date/heure d'expiration du token")
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Schema(description = "Indique si le token est révoqué")
    @Column(nullable = false)
    private boolean revoked = false;

    @Schema(description = "Date/heure de création du token")
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public RefreshToken() {
    }

    // --- Hooks automatiques ---
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
