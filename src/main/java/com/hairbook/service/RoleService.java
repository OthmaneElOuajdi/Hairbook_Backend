package com.hairbook.service;

import java.util.List;
import java.util.Optional;

import com.hairbook.entity.ERole;
import com.hairbook.entity.Role;

/**
 * Service pour la gestion des rôles utilisateurs.
 * Fournit des opérations CRUD de base pour les rôles.
 */
public interface RoleService {
    /**
     * Crée un nouveau rôle.
     *
     * @param role Le rôle à créer.
     * @return Le rôle créé.
     */
    Role create(Role role);

    /**
     * Trouve un rôle par son ID.
     *
     * @param id L'ID du rôle.
     * @return Un Optional contenant le rôle s'il est trouvé.
     */
    Optional<Role> findById(Long id);

    /**
     * Trouve un rôle par son nom (enum).
     *
     * @param name Le nom du rôle (type ERole).
     * @return Un Optional contenant le rôle s'il est trouvé.
     */
    Optional<Role> findByName(ERole name);

    /**
     * Récupère la liste de tous les rôles disponibles.
     *
     * @return Une liste de tous les rôles.
     */
    List<Role> findAll();

    /**
     * Supprime un rôle par son ID.
     *
     * @param id L'ID du rôle à supprimer.
     */
    void deleteById(Long id);
}
