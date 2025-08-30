package com.hairbook.service;

import java.util.List;
import java.util.Optional;

import com.hairbook.entity.ServiceItem;

/**
 * Service pour la gestion des prestations (services) proposées par le salon.
 * Fournit des opérations CRUD pour les services comme les coupes, colorations,
 * etc.
 */
public interface ServiceItemService {
    /**
     * Crée une nouvelle prestation.
     *
     * @param s La prestation à créer.
     * @return La prestation créée.
     */
    ServiceItem create(ServiceItem s);

    /**
     * Trouve une prestation par son ID.
     *
     * @param id L'ID de la prestation.
     * @return Un Optional contenant la prestation si elle est trouvée.
     */
    Optional<ServiceItem> findById(Long id);

    /**
     * Récupère la liste de toutes les prestations disponibles.
     *
     * @return Une liste de toutes les prestations.
     */
    List<ServiceItem> findAll();

    /**
     * Met à jour une prestation existante.
     *
     * @param s La prestation avec les informations mises à jour.
     * @return La prestation mise à jour.
     */
    ServiceItem update(ServiceItem s);

    /**
     * Supprime une prestation par son ID.
     *
     * @param id L'ID de la prestation à supprimer.
     */
    void deleteById(Long id);
}
