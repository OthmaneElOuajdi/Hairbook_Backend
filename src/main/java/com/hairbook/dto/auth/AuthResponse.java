package com.hairbook.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Réponse d'authentification contenant les jetons")
public class AuthResponse {

    @Schema(description = "Jeton d'accès JWT")
    private String accessToken;

    @Schema(description = "Jeton de rafraîchissement")
    private String refreshToken;

    @Schema(description = "Type de jeton")
    private String tokenType = "Bearer";

    @Schema(description = "Durée de validité du jeton d'accès en secondes")
    private long expiresIn;

    public AuthResponse() {
    }

    public AuthResponse(String accessToken, String refreshToken, long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
