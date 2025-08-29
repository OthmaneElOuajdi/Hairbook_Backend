package com.hairbook.repository;

import com.hairbook.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Dépôt pour gérer les opérations de base de données pour les entités AuditLog.
 * Fournit des méthodes CRUD pour l'historique d'audit.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
