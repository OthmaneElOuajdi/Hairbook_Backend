package com.hairbook.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration globale pour le Web MVC, y compris la gestion du CORS.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    /**
     * Configure le Cross-Origin Resource Sharing (CORS) pour l'application.
     * <p>
     * Cette configuration permet aux origines spécifiées dans la propriété
     * {@code app.cors.allowed-origins} d'accéder à l'API.
     * Elle est essentielle pour les applications front-end (SPA) hébergées sur un
     * domaine différent.
     *
     * @param registry le registre CORS à configurer.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
