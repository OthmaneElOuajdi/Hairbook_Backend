package com.hairbook.config;

import org.springframework.context.annotation.Configuration;

/**
 * Configuration du cache Redis pour optimiser les performances.
 * <p>
 * Définit les noms des caches et leurs configurations TTL respectives.
 * Les noms des caches sont centralisés dans la classe interne
 * {@link CacheNames}
 * pour garantir la cohérence dans toute l'application.
 */
@Configuration
public class CacheConfig {

    /**
     * Contient les noms des caches utilisés dans l'application.
     * Utiliser ces constantes avec les annotations @Cacheable, @CacheEvict, etc.
     */
    public static final class CacheNames {
        public static final String USERS = "users";
        public static final String SERVICES = "services";
        public static final String WORKING_HOURS = "working-hours";
        public static final String ROLES = "roles";
        public static final String RESERVATIONS = "reservations";
        public static final String LOYALTY_POINTS = "loyalty-points";
        public static final String NOTIFICATIONS = "notifications";
        public static final String STATISTICS = "statistics";

        private CacheNames() {
        }
    }
}
