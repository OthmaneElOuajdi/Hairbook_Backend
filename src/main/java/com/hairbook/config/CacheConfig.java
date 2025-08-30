package com.hairbook.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration du cache Redis pour optimiser les performances.
 * <p>
 * Définit les noms des caches et leurs configurations TTL respectives.
 * Les noms des caches sont centralisés dans la classe interne
 * {@link CacheNames}
 * pour garantir la cohérence dans toute l'application.
 */
@Configuration
@EnableCaching
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

    /**
     * Configuration du gestionnaire de cache Redis
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
                                .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        cacheConfigurations.put(CacheNames.USERS, defaultConfig.entryTtl(Duration.ofMinutes(30)));
        cacheConfigurations.put(CacheNames.SERVICES, defaultConfig.entryTtl(Duration.ofHours(1)));
        cacheConfigurations.put(CacheNames.WORKING_HOURS, defaultConfig.entryTtl(Duration.ofHours(24)));
        cacheConfigurations.put(CacheNames.ROLES, defaultConfig.entryTtl(Duration.ofHours(24)));
        cacheConfigurations.put(CacheNames.RESERVATIONS, defaultConfig.entryTtl(Duration.ofMinutes(5)));
        cacheConfigurations.put(CacheNames.LOYALTY_POINTS, defaultConfig.entryTtl(Duration.ofMinutes(15)));
        cacheConfigurations.put(CacheNames.NOTIFICATIONS, defaultConfig.entryTtl(Duration.ofMinutes(2)));
        cacheConfigurations.put(CacheNames.STATISTICS, defaultConfig.entryTtl(Duration.ofHours(1)));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    /**
     * Template Redis personnalisé pour les opérations avancées
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }
}
