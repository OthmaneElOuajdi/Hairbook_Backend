package com.hairbook.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur pour fournir des endpoints sur l'état de santé de l'application.
 */
@RestController
@RequestMapping("/api/health")
@Tag(name = "Health", description = "Vérification de l'état de l'application")
public class HealthController {

    /**
     * Fournit une vérification détaillée de l'état de santé de l'application.
     *
     * @return Une carte contenant l'état de santé, l'horodatage, le nom du service
     *         et la version.
     */
    @GetMapping
    @Operation(summary = "Vérifier l'état de santé de l'application")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "Hairbook API");
        health.put("version", "1.0.0");
        return ResponseEntity.ok(health);
    }

    /**
     * Un endpoint simple pour vérifier la connectivité de l'API.
     *
     * @return Une entité de réponse avec la chaîne "pong" pour indiquer que le
     *         service est joignable.
     */
    @GetMapping("/ping")
    @Operation(summary = "Ping simple pour vérifier la connectivité")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
}