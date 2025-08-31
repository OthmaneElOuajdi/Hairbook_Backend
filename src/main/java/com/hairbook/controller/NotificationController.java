package com.hairbook.controller;

import com.hairbook.entity.Notification;
import com.hairbook.entity.NotificationStatus;
import com.hairbook.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Contrôleur pour la gestion des notifications des utilisateurs.
 * Fournit des endpoints pour créer, récupérer et mettre à jour les
 * notifications.
 */
@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Gestion des notifications utilisateur")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Construit un NotificationController avec le service requis.
     *
     * @param notificationService Service pour la gestion de la logique des
     *                            notifications.
     */
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Crée une nouvelle notification.
     *
     * @param notification L'objet de notification à créer.
     * @return Une entité de réponse avec la notification créée.
     */
    @PostMapping
    @Operation(summary = "Créer une nouvelle notification")
    public ResponseEntity<Notification> create(@Valid @RequestBody Notification notification) {
        Notification created = notificationService.create(notification);
        return ResponseEntity.created(URI.create("/api/notifications/" + created.getId())).body(created);
    }

    /**
     * Récupère une notification par son ID.
     *
     * @param id L'ID de la notification à récupérer.
     * @return Une entité de réponse avec la notification, ou non trouvée si elle
     *         n'existe pas.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une notification par son ID")
    public ResponseEntity<Notification> findById(@PathVariable Long id) {
        return notificationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Récupère toutes les notifications pour un utilisateur spécifique.
     *
     * @param userId L'ID de l'utilisateur.
     * @return Une entité de réponse avec une liste des notifications de
     *         l'utilisateur.
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Récupérer toutes les notifications d'un utilisateur")
    public ResponseEntity<List<Notification>> findByUser(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Long userId) {
        List<Notification> notifications = notificationService.findByUser(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Marque une notification comme lue.
     *
     * @param id L'ID de la notification à marquer comme lue.
     * @return Une entité de réponse avec la notification mise à jour, ou non
     *         trouvée si elle n'existe pas.
     */
    @PutMapping("/{id}/mark-as-read")
    @Operation(summary = "Marquer une notification comme lue")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
        try {
            Notification updated = notificationService.markAsRead(id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Met à jour le statut d'une notification (par exemple, de UNREAD à READ).
     *
     * @param id     L'ID de la notification à mettre à jour.
     * @param status Le nouveau statut de la notification.
     * @return Une entité de réponse avec la notification mise à jour, ou non
     *         trouvée si elle n'existe pas.
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "Mettre à jour le statut d'une notification")
    public ResponseEntity<Notification> updateStatus(
            @PathVariable Long id,
            @Parameter(description = "Nouveau statut") @RequestParam NotificationStatus status) {
        try {
            Notification updated = notificationService.updateStatus(id, status);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}