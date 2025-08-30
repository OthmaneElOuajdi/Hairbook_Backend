package com.hairbook.service;

import java.util.List;

import com.hairbook.entity.AuditLog;

/**
 * Service pour la gestion des logs d'audit.
 * Enregistre les actions des utilisateurs et du système pour la traçabilité et
 * la sécurité.
 */
public interface AuditLogService {
    /**
     * Enregistre une action effectuée par un utilisateur.
     *
     * @param userId     L'ID de l'utilisateur qui a effectué l'action.
     * @param entityName Le nom de l'entité affectée (ex: "Reservation").
     * @param entityId   L'ID de l'entité affectée.
     * @param action     L'action effectuée (ex: "CREATE", "DELETE").
     * @param details    Détails supplémentaires sur l'action.
     * @return L'objet AuditLog qui a été sauvegardé.
     */
    AuditLog logUserAction(Long userId, String entityName, Long entityId, String action, String details);

    /**
     * Enregistre une action effectuée par le système (ex: tâche planifiée).
     *
     * @param entityName Le nom de l'entité affectée.
     * @param entityId   L'ID de l'entité affectée.
     * @param action     L'action effectuée.
     * @param details    Détails supplémentaires sur l'action.
     * @return L'objet AuditLog qui a été sauvegardé.
     */
    AuditLog logSystemAction(String entityName, Long entityId, String action, String details);

    /**
     * Récupère les N derniers logs d'audit, triés par date de création descendante.
     *
     * @param limit Le nombre maximum de logs à récupérer.
     * @return Une liste des derniers AuditLog.
     */
    List<AuditLog> last(int limit);
}
