package com.hairbook.config;

import com.hairbook.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuration de la sécurité de l'application.
 * 
 * Cette classe définit :
 * <ul>
 * <li>Le chiffrement des mots de passe avec BCrypt.</li>
 * <li>La configuration CORS pour autoriser certaines origines.</li>
 * <li>La gestion des sessions en mode stateless.</li>
 * <li>L’intégration du filtre JWT pour authentifier les requêtes.</li>
 * <li>Les règles d’accès aux différentes ressources REST.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Liste des origines autorisées pour les requêtes CORS.
     * Chargée depuis la configuration de l’application (application.yml ou
     * properties).
     */
    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    /**
     * Fournit un encodeur de mots de passe basé sur BCrypt.
     * 
     * @return {@link PasswordEncoder} utilisant BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Fournit le gestionnaire d'authentification utilisé par Spring Security.
     * 
     * @param authenticationConfiguration configuration d’authentification de
     *                                    Spring.
     * @return {@link AuthenticationManager} configuré.
     * @throws Exception en cas d’erreur lors de la récupération.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Définit la configuration CORS pour autoriser les requêtes venant
     * des origines spécifiées dans la configuration de l’application.
     * 
     * @return {@link CorsConfigurationSource} avec les règles CORS.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    /**
     * Définit la chaîne de filtres de sécurité.
     * 
     * - Désactive CSRF (non nécessaire pour les API REST stateless).
     * - Applique la configuration CORS.
     * - Autorise l’accès libre aux endpoints d’authentification et de services
     * publics.
     * - Protège toutes les autres routes par authentification.
     * - Ajoute le filtre JWT pour vérifier les tokens avant le filtre
     * UsernamePassword.
     * - Configure une gestion de session sans état (STATELESS).
     *
     * @param http                    objet {@link HttpSecurity} pour configurer les
     *                                règles.
     * @param jwtAuthenticationFilter filtre personnalisé pour valider les JWT.
     * @return {@link SecurityFilterChain} représentant la configuration.
     * @throws Exception en cas d’erreur de configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter)
            throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/services/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter,
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
