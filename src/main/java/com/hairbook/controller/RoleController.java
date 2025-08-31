package com.hairbook.controller;

import com.hairbook.entity.ERole;
import com.hairbook.entity.Role;
import com.hairbook.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Contrôleur pour la gestion des rôles utilisateur.
 * Fournit des endpoints pour créer, récupérer et supprimer des rôles.
 */
@RestController
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "Gestion des rôles utilisateur")
public class RoleController {

    private final RoleService roleService;

    /**
     * Construit un RoleController avec le service requis.
     *
     * @param roleService Service pour la gestion de la logique des rôles.
     */
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * Crée un nouveau rôle utilisateur.
     *
     * @param role L'objet rôle à créer.
     * @return Une entité de réponse avec le rôle créé.
     */
    @PostMapping
    @Operation(summary = "Créer un nouveau rôle")
    public ResponseEntity<Role> create(@Valid @RequestBody Role role) {
        Role created = roleService.create(role);
        return ResponseEntity.created(URI.create("/api/roles/" + created.getId())).body(created);
    }

    /**
     * Récupère une liste de tous les rôles disponibles.
     *
     * @return Une entité de réponse avec une liste de tous les rôles.
     */
    @GetMapping
    @Operation(summary = "Lister tous les rôles")
    public ResponseEntity<List<Role>> findAll() {
        List<Role> roles = roleService.findAll();
        return ResponseEntity.ok(roles);
    }

    /**
     * Récupère un rôle par son ID.
     *
     * @param id L'ID du rôle à récupérer.
     * @return Une entité de réponse avec le rôle, ou non trouvé s'il n'existe pas.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un rôle par son ID")
    public ResponseEntity<Role> findById(@PathVariable Long id) {
        return roleService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Récupère un rôle par son nom.
     *
     * @param name Le nom du rôle à récupérer (par exemple, ROLE_USER, ROLE_ADMIN).
     * @return Une entité de réponse avec le rôle, ou non trouvé s'il n'existe pas.
     */
    @GetMapping("/by-name")
    @Operation(summary = "Récupérer un rôle par son nom")
    public ResponseEntity<Role> findByName(
            @Parameter(description = "Nom du rôle") @RequestParam ERole name) {
        return roleService.findByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Supprime un rôle par son ID.
     *
     * @param id L'ID du rôle à supprimer.
     * @return Une entité de réponse indiquant le succès sans contenu.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un rôle")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}