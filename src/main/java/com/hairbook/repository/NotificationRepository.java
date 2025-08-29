package com.hairbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.hairbook.entity.Notification;
import com.hairbook.entity.NotificationStatus;

import java.util.List;

/**
 * Dépôt pour la gestion des notifications envoyées aux utilisateurs.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Recherche les notifications par leur statut (par exemple, ENVOYÉ, ÉCHOUÉ).
     *
     * @param status Le statut de la notification à rechercher.
     * @return Une liste de notifications correspondant au statut.
     */
    List<Notification> findByStatus(NotificationStatus status);

    /**
     * Recherche toutes les notifications pour un utilisateur donné, triées par date d'envoi décroissante.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @return Une liste de notifications pour l'utilisateur.
     */
    List<Notification> findByUser_IdOrderBySentAtDesc(Long userId);
}
