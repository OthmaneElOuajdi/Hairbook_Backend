package com.hairbook.controller;

import com.hairbook.entity.User;
import com.hairbook.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Contrôleur pour la gestion des comptes utilisateurs.
 * Fournit des endpoints CRUD pour les utilisateurs.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "CRUD des utilisateurs")
public class UserController {

    private final UserService userService;

    /**
     * Construit un UserController avec le service requis.
     *
     * @param userService Service pour la gestion de la logique utilisateur.
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Récupère une liste de tous les utilisateurs.
     *
     * @return Une entité de réponse avec une liste de tous les utilisateurs.
     */
    @GetMapping
    @Operation(summary = "Lister tous les utilisateurs")
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    /**
     * Récupère un utilisateur par son ID.
     *
     * @param id L'ID de l'utilisateur à récupérer.
     * @return Une entité de réponse avec l'utilisateur, ou non trouvé s'il n'existe
     *         pas.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un utilisateur par son id")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Récupère un utilisateur par son adresse e-mail.
     *
     * @param email L'adresse e-mail de l'utilisateur à récupérer.
     * @return Une entité de réponse avec l'utilisateur, ou non trouvé s'il n'existe
     *         pas.
     */
    @GetMapping("/by-email")
    @Operation(summary = "Récupérer un utilisateur par email")
    public ResponseEntity<User> findByEmail(
            @Parameter(description = "Email de l'utilisateur") @RequestParam String email) {
        return userService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crée un nouvel utilisateur.
     * Note : Pour l'inscription publique, utilisez l'endpoint /api/auth/register.
     * Ceci est pour un usage administrateur.
     *
     * @param user L'objet utilisateur à créer.
     * @return Une entité de réponse avec l'utilisateur créé.
     */
    @PostMapping
    @Operation(summary = "Créer un nouvel utilisateur")
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        User created = userService.create(user);
        return ResponseEntity.created(URI.create("/api/users/" + created.getId())).body(created);
    }

    /**
     * Met à jour un utilisateur existant.
     *
     * @param id   L'ID de l'utilisateur à mettre à jour.
     * @param user L'objet utilisateur mis à jour.
     * @return Une entité de réponse avec l'utilisateur mis à jour.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un utilisateur")
    public ResponseEntity<User> update(@PathVariable Long id, @Valid @RequestBody User user) {
        // sécurité : on impose l'id du path
        user.setId(id);
        User updated = userService.update(user);
        return ResponseEntity.ok(updated);
    }

    /**
     * Supprime un utilisateur par son ID.
     *
     * @param id L'ID de l'utilisateur à supprimer.
     * @return Une entité de réponse indiquant le succès sans contenu.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un utilisateur")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
