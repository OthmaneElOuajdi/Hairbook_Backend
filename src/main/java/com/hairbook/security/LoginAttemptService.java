package com.hairbook.security;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Service;

/**
 * Service de gestion des tentatives de connexion pour prévenir les attaques par force brute.
 * Ce service suit les tentatives échouées par adresse IP et bloque temporairement une IP
 * après un nombre excessif de tentatives.
 */
@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 15;

    private final ConcurrentMap<String, AttemptInfo> attemptCache = new ConcurrentHashMap<>();

    /**
     * Enregistre une tentative de connexion échouée pour une adresse IP donnée.
     * Si le nombre maximal de tentatives est atteint, l'IP est bloquée.
     *
     * @param ipAddress L'adresse IP de l'appelant.
     */
    public void recordFailedAttempt(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            return;
        }

        AttemptInfo attemptInfo = attemptCache.computeIfAbsent(ipAddress, k -> new AttemptInfo());

        if (attemptInfo.isLockoutExpired()) {
            attemptInfo.reset();
        }

        attemptInfo.incrementFailedAttempts();

        if (attemptInfo.getFailedAttempts() >= MAX_ATTEMPTS) {
            attemptInfo.lockout();
        }
    }

    /**
     * Réinitialise le compteur de tentatives pour une adresse IP après une connexion réussie.
     *
     * @param ipAddress L'adresse IP de l'appelant.
     */
    public void recordSuccessfulAttempt(String ipAddress) {
        // Ignorer les adresses IP nulles ou vides
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            return;
        }
        attemptCache.remove(ipAddress);
    }

    /**
     * Vérifie si une adresse IP est actuellement bloquée.
     *
     * @param ipAddress L'adresse IP à vérifier.
     * @return true si l'IP est bloquée, sinon false.
     */
    public boolean isBlocked(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            return false;
        }

        AttemptInfo attemptInfo = attemptCache.get(ipAddress);

        if (attemptInfo == null) {
            return false;
        }

        if (attemptInfo.isLockoutExpired()) {
            attemptCache.remove(ipAddress);
            return false;
        }

        return attemptInfo.isLocked();
    }

    /**
     * Renvoie le nombre de tentatives de connexion restantes pour une adresse IP avant le blocage.
     *
     * @param ipAddress L'adresse IP.
     * @return Le nombre de tentatives restantes.
     */
    public int getRemainingAttempts(String ipAddress) {
        AttemptInfo attemptInfo = attemptCache.get(ipAddress);

        if (attemptInfo == null || attemptInfo.isLockoutExpired()) {
            return MAX_ATTEMPTS;
        }

        return Math.max(0, MAX_ATTEMPTS - attemptInfo.getFailedAttempts());
    }

    /**
     * Calcule le temps de blocage restant en minutes pour une adresse IP.
     *
     * @param ipAddress L'adresse IP bloquée.
     * @return Le nombre de minutes restantes avant le déblocage, ou 0 si non bloquée.
     */
    public long getRemainingLockoutMinutes(String ipAddress) {
        AttemptInfo attemptInfo = attemptCache.get(ipAddress);

        if (attemptInfo == null || !attemptInfo.isLocked()) {
            return 0;
        }

        return ChronoUnit.MINUTES.between(LocalDateTime.now(), attemptInfo.getLockoutExpiry());
    }

    /**
     * Classe interne pour stocker les informations sur les tentatives de connexion d'une IP.
     */
    private static class AttemptInfo {
        private int failedAttempts = 0;
        private LocalDateTime lockoutExpiry;
        private boolean locked = false;

        public void incrementFailedAttempts() {
            this.failedAttempts++;
        }

        public void lockout() {
            this.locked = true;
            this.lockoutExpiry = LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES);
        }

        public void reset() {
            this.failedAttempts = 0;
            this.locked = false;
            this.lockoutExpiry = null;
        }

        public boolean isLockoutExpired() {
            return lockoutExpiry != null && LocalDateTime.now().isAfter(lockoutExpiry);
        }

        public int getFailedAttempts() {
            return failedAttempts;
        }

        public boolean isLocked() {
            return locked && !isLockoutExpired();
        }

        public LocalDateTime getLockoutExpiry() {
            return lockoutExpiry;
        }
    }
}
