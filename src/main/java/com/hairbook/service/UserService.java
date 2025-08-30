package com.hairbook.service;

import com.hairbook.dto.auth.AuthRequest;
import com.hairbook.dto.auth.AuthResponse;
import com.hairbook.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des utilisateurs et de l'authentification.
 * Fournit des méthodes pour le CRUD des utilisateurs, ainsi que pour
 * l'enregistrement et l'authentification.
 */
public interface UserService {

    // --- Auth ---
    /**
     * Authentifie un utilisateur avec une adresse email et un mot de passe.
     *
     * @param request L'objet de demande d'authentification contenant l'email et le
     *                mot de passe.
     * @return Une réponse d'authentification contenant le token JWT.
     */
    AuthResponse authenticate(AuthRequest request);

    /**
     * Enregistre un nouvel utilisateur dans le système.
     *
     * @param user L'utilisateur à enregistrer. Le mot de passe doit être en clair.
     * @return L'utilisateur enregistré avec son ID et son mot de passe haché.
     */
    User register(User user);

    /**
     * Récupère l'utilisateur actuellement authentifié à partir d'un token JWT.
     *
     * @param token Le token JWT (sans le préfixe "Bearer ").
     * @return L'utilisateur correspondant au token.
     */
    User getCurrentUserFromToken(String token);

    // --- CRUD ---
    /**
     * Crée un nouvel utilisateur.
     *
     * @param user L'utilisateur à créer.
     * @return L'utilisateur créé.
     */
    User create(User user);

    /**
     * Trouve un utilisateur par son ID.
     *
     * @param id L'ID de l'utilisateur.
     * @return Un Optional contenant l'utilisateur s'il est trouvé.
     */
    Optional<User> findById(Long id);

    /**
     * Trouve un utilisateur par son adresse email.
     *
     * @param email L'adresse email de l'utilisateur.
     * @return Un Optional contenant l'utilisateur s'il est trouvé.
     */
    Optional<User> findByEmail(String email);

    /**
     * Récupère la liste de tous les utilisateurs.
     *
     * @return Une liste de tous les utilisateurs.
     */
    List<User> findAll();

    /**
     * Met à jour les informations d'un utilisateur existant.
     *
     * @param user L'utilisateur avec les informations mises à jour.
     * @return L'utilisateur mis à jour.
     */
    User update(User user);

    /**
     * Supprime un utilisateur par son ID.
     *
     * @param id L'ID de l'utilisateur à supprimer.
     */
    void deleteById(Long id);
}
