package com.hairbook.service;

import java.util.List;
import java.util.Optional;

import com.hairbook.entity.Notification;
import com.hairbook.entity.NotificationStatus;

/**
 * Service pour la gestion des notifications envoyées aux utilisateurs.
 */
public interface NotificationService {
    /**
     * Crée et envoie une nouvelle notification.
     *
     * @param n La notification à créer.
     * @return La notification créée.
     */
    Notification create(Notification n);

    /**
     * Trouve une notification par son ID.
     *
     * @param id L'ID de la notification.
     * @return Un Optional contenant la notification si elle est trouvée.
     */
    Optional<Notification> findById(Long id);

    /**
     * Récupère toutes les notifications pour un utilisateur donné.
     *
     * @param userId L'ID de l'utilisateur.
     * @return Une liste des notifications de l'utilisateur.
     */
    List<Notification> findByUser(Long userId);

    /**
     * Marque une notification comme lue.
     *
     * @param id L'ID de la notification à marquer comme lue.
     * @return La notification mise à jour.
     */
    Notification markAsRead(Long id);

    /**
     * Met à jour le statut d'une notification (ex: lue, archivée).
     *
     * @param id     L'ID de la notification.
     * @param status Le nouveau statut.
     * @return La notification mise à jour.
     */
    Notification updateStatus(Long id, NotificationStatus status);
}
