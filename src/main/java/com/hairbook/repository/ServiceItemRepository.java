package com.hairbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hairbook.entity.ServiceItem;

/**
 * Dépôt pour la gestion des services proposés (par exemple, coupe, couleur).
 * Fournit des méthodes CRUD de base pour les entités ServiceItem.
 */
@Repository
public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long> {
}
