package com.hairbook.service.impl;

import com.hairbook.entity.Notification;
import com.hairbook.entity.NotificationStatus;
import com.hairbook.repository.NotificationRepository;
import com.hairbook.service.NotificationService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation du service pour la gestion des notifications utilisateur.
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repo;

    /**
     * Constructeur pour l'injection du repository des notifications.
     *
     * @param repo Le repository pour les entités Notification.
     */
    public NotificationServiceImpl(NotificationRepository repo) {
        this.repo = repo;
    }

    @Override
    public Notification create(Notification n) {
        return repo.save(n);
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public List<Notification> findByUser(Long userId) {
        return repo.findByUser_IdOrderBySentAtDesc(userId);
    }

    /**
     * Marque une notification comme lue.
     *
     * @param id L'ID de la notification à marquer.
     * @return La notification mise à jour.
     * @throws IllegalArgumentException si la notification n'est pas trouvée.
     */
    @Override
    public Notification markAsRead(Long id) {
        Notification n = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        n.setStatus(NotificationStatus.READ);
        n.setReadAt(LocalDateTime.now());
        return repo.save(n);
    }

    /**
     * Met à jour le statut d'une notification.
     *
     * @param id     L'ID de la notification.
     * @param status Le nouveau statut de la notification.
     * @return La notification mise à jour.
     * @throws IllegalArgumentException si la notification n'est pas trouvée.
     */
    @Override
    public Notification updateStatus(Long id, NotificationStatus status) {
        Notification n = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        n.setStatus(status);
        return repo.save(n);
    }
}
