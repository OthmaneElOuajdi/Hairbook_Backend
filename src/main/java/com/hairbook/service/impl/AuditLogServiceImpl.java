package com.hairbook.service.impl;

import com.hairbook.entity.AuditLog;
import com.hairbook.entity.User;
import com.hairbook.repository.AuditLogRepository;
import com.hairbook.repository.UserRepository;
import com.hairbook.service.AuditLogService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implémentation du service d'audit. Gère la logique de création des logs.
 */
@Service
@Transactional
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    /**
     * Constructeur pour l'injection de dépendances.
     *
     * @param auditLogRepository Le repository pour les logs d'audit.
     * @param userRepository     Le repository pour les utilisateurs.
     */
    public AuditLogServiceImpl(AuditLogRepository auditLogRepository,
            UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AuditLog logUserAction(Long userId, String entityName, Long entityId, String action, String details) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        AuditLog log = new AuditLog();
        log.setUser(user);
        log.setEntityName(entityName);
        log.setEntityId(entityId);
        log.setAction(action);
        log.setDetails(details);
        return auditLogRepository.save(log);
    }

    @Override
    public AuditLog logSystemAction(String entityName, Long entityId, String action, String details) {
        AuditLog log = new AuditLog();
        log.setEntityName(entityName);
        log.setEntityId(entityId);
        log.setAction(action);
        log.setDetails(details);
        return auditLogRepository.save(log);
    }

    @Override
    public List<AuditLog> last(int limit) {
        return auditLogRepository.findAll(PageRequest.of(0, Math.max(1, limit))).getContent();
    }
}
