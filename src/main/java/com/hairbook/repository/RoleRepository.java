package com.hairbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hairbook.entity.ERole;
import com.hairbook.entity.Role;

import java.util.Optional;

/**
 * Dépôt pour la gestion des rôles (par exemple, USER, ADMIN).
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * Recherche un rôle par son nom (enum ERole).
     *
     * @param name Le nom du rôle à trouver.
     * @return Un Optional contenant le rôle s'il est trouvé.
     */
    Optional<Role> findByName(ERole name);
}
