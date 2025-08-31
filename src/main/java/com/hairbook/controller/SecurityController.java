package com.hairbook.controller;

import com.hairbook.security.LoginAttemptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur pour la surveillance et la gestion des aspects de sécurité de
 * l'application.
 * Fournit des endpoints pour vérifier l'état de la sécurité et gérer le blocage
 * d'IP.
 */
@RestController
@RequestMapping("/api/security")
@Tag(name = "Security", description = "Gestion de la sécurité et surveillance")
public class SecurityController {

    private final LoginAttemptService loginAttemptService;

    /**
     * Construit un SecurityController avec le service requis.
     *
     * @param loginAttemptService Service pour le suivi et le blocage des tentatives
     *                            de connexion.
     */
    public SecurityController(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    /**
     * Vérifie l'état de sécurité pour l'adresse IP du client.
     * Cela inclut si l'IP est actuellement bloquée, le nombre de tentatives de
     * connexion restantes,
     * et le temps de verrouillage restant si elle est bloquée.
     *
     * @param request La requête servlet HTTP entrante.
     * @return Une carte contenant les détails de l'état de sécurité pour l'IP du
     *         client.
     */
    @GetMapping("/status")
    @Operation(summary = "Vérifier le statut de sécurité", description = "Vérifie si l'IP actuelle est bloquée et le nombre de tentatives restantes")
    public ResponseEntity<Map<String, Object>> getSecurityStatus(HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);

        Map<String, Object> status = new HashMap<>();
        status.put("ip", clientIp);
        status.put("isBlocked", loginAttemptService.isBlocked(clientIp));
        status.put("remainingAttempts", loginAttemptService.getRemainingAttempts(clientIp));

        if (loginAttemptService.isBlocked(clientIp)) {
            status.put("remainingLockoutMinutes", loginAttemptService.getRemainingLockoutMinutes(clientIp));
        }

        return ResponseEntity.ok(status);
    }

    /**
     * Permet à un administrateur de débloquer une adresse IP spécifiée.
     * Cela réinitialise le compteur de tentatives de connexion pour l'IP donnée.
     * Cet endpoint est restreint aux utilisateurs ayant le rôle 'ADMIN'.
     *
     * @param ip L'adresse IP à débloquer.
     * @return Un message de confirmation indiquant le résultat de l'opération.
     */
    @PostMapping("/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Débloquer une IP", description = "Permet à un administrateur de débloquer une IP")
    public ResponseEntity<Map<String, String>> unblockIp(
            @Parameter(description = "Adresse IP à débloquer") @RequestParam String ip) {

        loginAttemptService.recordSuccessfulAttempt(ip);

        Map<String, String> response = new HashMap<>();
        response.put("message", "IP " + ip + " débloquée avec succès");
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }

    /**
     * Récupère l'adresse IP du client à partir de la requête.
     * Il vérifie les en-têtes 'X-Forwarded-For' et 'X-Real-IP' pour supporter les
     * proxys.
     *
     * @param request La requête servlet HTTP entrante.
     * @return L'adresse IP du client sous forme de chaîne de caractères.
     */
    private String getClientIpAddress(HttpServletRequest request) {
        // Vérifier les headers de proxy
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
