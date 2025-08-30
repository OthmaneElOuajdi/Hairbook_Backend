package com.hairbook.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Classe de configuration pour lier les propriétés JWT de
 * application.properties.
 * Fournit une configuration centralisée et type-safe pour la gestion des tokens
 * JWT.
 */
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    /**
     * La clé secrète utilisée pour signer les tokens JWT. Doit être une chaîne
     * longue et aléatoire, encodée en Base64.
     */
    private String secret;

    /**
     * Durée de validité du token d'accès en millisecondes.
     */
    private long expirationMs;

    /**
     * Durée de validité du token de rafraîchissement en millisecondes.
     */
    private long refreshTokenExpirationMs;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    public void setExpirationMs(long expirationMs) {
        this.expirationMs = expirationMs;
    }

    public long getRefreshTokenExpirationMs() {
        return refreshTokenExpirationMs;
    }

    public void setRefreshTokenExpirationMs(long refreshTokenExpirationMs) {
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }
}
