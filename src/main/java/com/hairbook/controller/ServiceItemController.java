package com.hairbook.controller;

import com.hairbook.entity.ServiceItem;
import com.hairbook.service.ServiceItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Contrôleur pour la gestion des prestations offertes par le salon.
 * Fournit des endpoints CRUD pour les prestations.
 */
@RestController
@RequestMapping("/api/services")
@Tag(name = "Services", description = "CRUD des prestations (ServiceItem)")
public class ServiceItemController {

    private final ServiceItemService serviceItemService;

    /**
     * Construit un ServiceItemController avec le service requis.
     *
     * @param serviceItemService Service pour la gestion de la logique des
     *                           prestations.
     */
    public ServiceItemController(ServiceItemService serviceItemService) {
        this.serviceItemService = serviceItemService;
    }

    /**
     * Récupère une liste de toutes les prestations disponibles.
     *
     * @return Une entité de réponse avec une liste de toutes les prestations.
     */
    @GetMapping
    @Operation(summary = "Lister toutes les prestations")
    public ResponseEntity<List<ServiceItem>> findAll() {
        return ResponseEntity.ok(serviceItemService.findAll());
    }

    /**
     * Récupère une prestation par son ID.
     *
     * @param id L'ID de la prestation à récupérer.
     * @return Une entité de réponse avec la prestation, ou non trouvée si elle
     *         n'existe pas.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une prestation par id")
    public ResponseEntity<ServiceItem> findById(@PathVariable Long id) {
        return serviceItemService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crée une nouvelle prestation.
     *
     * @param item L'objet prestation à créer.
     * @return Une entité de réponse avec la prestation créée.
     */
    @PostMapping
    @Operation(summary = "Créer une nouvelle prestation")
    public ResponseEntity<ServiceItem> create(@Valid @RequestBody ServiceItem item) {
        ServiceItem created = serviceItemService.create(item);
        return ResponseEntity.created(URI.create("/api/services/" + created.getId())).body(created);
    }

    /**
     * Met à jour une prestation existante.
     *
     * @param id   L'ID de la prestation à mettre à jour.
     * @param item L'objet prestation mis à jour.
     * @return Une entité de réponse avec la prestation mise à jour.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une prestation")
    public ResponseEntity<ServiceItem> update(@PathVariable Long id, @Valid @RequestBody ServiceItem item) {
        item.setId(id); // impose l'id du path
        ServiceItem updated = serviceItemService.update(item);
        return ResponseEntity.ok(updated);
    }

    /**
     * Supprime une prestation par son ID.
     *
     * @param id L'ID de la prestation à supprimer.
     * @return Une entité de réponse indiquant le succès sans contenu.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une prestation")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviceItemService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
