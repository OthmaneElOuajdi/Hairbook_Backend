package com.hairbook.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Service de gestion des utilisateurs.
 * <p>
 * Ce service fournit des opérations CRUD et des méthodes de recherche
 * pour gérer les utilisateurs du système (clients, staff, admins).
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BlacklistService blacklistService;
    private final EmailService emailService;
    private final FileStorageService fileStorageService;

    /**
     * Récupère un utilisateur par son identifiant.
     *
     * @param id identifiant de l'utilisateur
     * @return User trouvé
     * @throws IllegalArgumentException si l'utilisateur n'existe pas
     */
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    /**
     * Récupère un utilisateur par son email.
     *
     * @param email adresse email
     * @return User trouvé
     * @throws IllegalArgumentException si l'utilisateur n'existe pas
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    /**
     * Récupère tous les utilisateurs.
     *
     * @return liste de tous les utilisateurs
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Récupère tous les utilisateurs actifs.
     * Utilise la méthode findByActiveTrue() du repository.
     *
     * @return liste des utilisateurs actifs
     */
    public List<User> getActiveUsers() {
        return userRepository.findByActiveTrue();
    }

    /**
     * Récupère tous les utilisateurs ayant un rôle spécifique.
     * Utilise la méthode findByRole() du repository.
     *
     * @param role nom du rôle (ex: "ROLE_CLIENT", "ROLE_STAFF", "ROLE_ADMIN")
     * @return liste des utilisateurs avec ce rôle
     */
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    /**
     * Récupère tous les utilisateurs actifs ayant un rôle spécifique.
     * Utilise la méthode findActiveByRole() du repository.
     *
     * @param role nom du rôle
     * @return liste des utilisateurs actifs avec ce rôle
     */
    public List<User> getActiveUsersByRole(String role) {
        return userRepository.findActiveByRole(role);
    }

    /**
     * Récupère tous les clients actifs.
     *
     * @return liste des clients actifs
     */
    public List<User> getActiveClients() {
        return getActiveUsersByRole("ROLE_CLIENT");
    }

    /**
     * Récupère tous les membres du staff actifs.
     *
     * @return liste du staff actif
     */
    public List<User> getActiveStaff() {
        return getActiveUsersByRole("ROLE_STAFF");
    }

    /**
     * Récupère tous les administrateurs actifs.
     *
     * @return liste des admins actifs
     */
    public List<User> getActiveAdmins() {
        return getActiveUsersByRole("ROLE_ADMIN");
    }

    /**
     * Récupère tous les clients (actifs ou non).
     *
     * @return liste de tous les clients
     */
    public List<User> getAllClients() {
        return getUsersByRole("ROLE_CLIENT");
    }

    /**
     * Récupère tous les membres du staff (actifs ou non).
     *
     * @return liste de tout le staff
     */
    public List<User> getAllStaff() {
        return getUsersByRole("ROLE_STAFF");
    }

    /**
     * Récupère tous les administrateurs (actifs ou non).
     *
     * @return liste de tous les admins
     */
    public List<User> getAllAdmins() {
        return getUsersByRole("ROLE_ADMIN");
    }

    /**
     * Met à jour les informations d'un utilisateur.
     *
     * @param id   identifiant de l'utilisateur
     * @param user nouvelles données
     * @return User mis à jour
     */
    @Transactional
    public User updateUser(UUID id, User user) {
        User existing = getUserById(id);

        if (user.getFirstName() != null) {
            existing.setFirstName(user.getFirstName());
        }
        if (user.getLastName() != null) {
            existing.setLastName(user.getLastName());
        }
        if (user.getPhone() != null) {
            existing.setPhone(user.getPhone());
        }
        if (user.getLoyaltyPoints() != null) {
            existing.setLoyaltyPoints(user.getLoyaltyPoints());
        }

        User updated = userRepository.save(existing);
        log.info("User updated: {}", id);
        return updated;
    }

    /**
     * Active un utilisateur.
     *
     * @param id identifiant de l'utilisateur
     * @return User activé
     */
    @Transactional
    public User activateUser(UUID id) {
        User user = getUserById(id);
        user.setActive(true);
        User activated = userRepository.save(user);
        log.info("User activated: {}", id);
        return activated;
    }

    /**
     * Désactive un utilisateur.
     *
     * @param id identifiant de l'utilisateur
     * @return User désactivé
     */
    @Transactional
    public User deactivateUser(UUID id) {
        User user = getUserById(id);
        user.setActive(false);
        User deactivated = userRepository.save(user);
        log.info("User deactivated: {}", id);
        return deactivated;
    }

    /**
     * Vérifie si un email est déjà utilisé.
     *
     * @param email adresse email à vérifier
     * @return true si l'email existe déjà
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Compte le nombre total d'utilisateurs.
     *
     * @return nombre d'utilisateurs
     */
    public long countUsers() {
        return userRepository.count();
    }

    /**
     * Compte le nombre d'utilisateurs actifs.
     *
     * @return nombre d'utilisateurs actifs
     */
    public long countActiveUsers() {
        return getActiveUsers().size();
    }

    /**
     * Compte le nombre d'utilisateurs par rôle.
     *
     * @param role nom du rôle
     * @return nombre d'utilisateurs avec ce rôle
     */
    public long countUsersByRole(String role) {
        return getUsersByRole(role).size();
    }

    /**
     * Supprime un utilisateur (soft delete - désactivation recommandée).
     *
     * @param id identifiant de l'utilisateur
     */
    @Transactional
    public void deleteUser(UUID id) {
        User user = getUserById(id);
        userRepository.delete(user);
        log.info("User deleted: {}", id);
    }

    /**
     * Met à jour le profil d'un utilisateur (prénom, nom, téléphone).
     *
     * @param userId identifiant de l'utilisateur
     * @param request nouvelles données du profil
     * @return User mis à jour
     */
    @Transactional
    public User updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = getUserById(userId);

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());

        User updated = userRepository.save(user);
        log.info("Profile updated for user: {}", userId);
        return updated;
    }

    /**
     * Crée un nouvel utilisateur (par un administrateur).
     *
     * @param request données du nouvel utilisateur
     * @return utilisateur créé
     * @throws IllegalArgumentException si l'email existe déjà
     */
    @Transactional
    public User createUser(CreateUserRequest request) {
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Stocker le mot de passe en clair temporairement pour l'email
        String plainPassword = request.getPassword();

        // Créer l'utilisateur
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .active(request.getActive() != null ? request.getActive() : true)
                .emailVerified(request.getEmailVerified() != null ? request.getEmailVerified() : true)
                .roles(request.getRoles() != null && !request.getRoles().isEmpty()
                        ? request.getRoles()
                        : Set.of("ROLE_CLIENT"))
                .build();

        User savedUser = userRepository.save(user);
        log.info("User created by admin: {}", savedUser.getEmail());

        // Envoyer l'email avec les identifiants
        emailService.sendNewAccountCredentials(
                savedUser.getEmail(),
                savedUser.getFirstName(),
                plainPassword
        );

        return savedUser;
    }

    /**
     * Change le mot de passe d'un utilisateur.
     *
     * @param userId identifiant de l'utilisateur
     * @param request contient l'ancien et le nouveau mot de passe
     * @throws IllegalArgumentException si l'ancien mot de passe est incorrect
     */
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = getUserById(userId);

        // Vérifier que l'ancien mot de passe est correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Mettre à jour avec le nouveau mot de passe
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed for user: {}", userId);
    }

    /**
     * Bloque un utilisateur et ajoute son email et téléphone à la blacklist.
     *
     * @param userId identifiant de l'utilisateur à bloquer
     * @param adminEmail email de l'admin qui effectue le blocage
     * @param reason raison du blocage
     * @throws IllegalArgumentException si l'utilisateur n'existe pas
     */
    @Transactional
    public void blockUser(UUID userId, String adminEmail, String reason) {
        User user = getUserById(userId);
        User admin = getUserByEmail(adminEmail);

        // Désactiver l'utilisateur
        user.setActive(false);
        userRepository.save(user);

        // Ajouter à la blacklist
        blacklistService.addToBlacklist(user, admin.getId(), reason);

        log.info("User {} blocked by admin {} with reason: {}", user.getEmail(), adminEmail, reason);
    }

    /**
     * Programme la suppression du compte utilisateur dans 30 jours.
     * Le compte est immédiatement désactivé.
     *
     * @param userId identifiant de l'utilisateur
     * @return utilisateur mis à jour
     */
    @Transactional
    public User scheduleDeletion(UUID userId) {
        User user = getUserById(userId);

        // Programmer la suppression dans 30 jours
        user.setDeletionScheduledAt(LocalDateTime.now().plusDays(30));
        user.setActive(false);

        User updated = userRepository.save(user);
        log.info("Account deletion scheduled for user {} at {}", user.getEmail(), user.getDeletionScheduledAt());

        return updated;
    }

    /**
     * Annule la suppression programmée du compte utilisateur.
     * Réactive le compte.
     *
     * @param userId identifiant de l'utilisateur
     * @return utilisateur mis à jour
     */
    @Transactional
    public User cancelDeletion(UUID userId) {
        User user = getUserById(userId);

        if (user.getDeletionScheduledAt() != null) {
            user.setDeletionScheduledAt(null);
            user.setActive(true);

            User updated = userRepository.save(user);
            log.info("Account deletion cancelled for user {}", user.getEmail());

            return updated;
        }

        return user;
    }

    /**
     * Met à jour la photo de profil d'un utilisateur.
     *
     * @param userId identifiant de l'utilisateur
     * @param profilePicturePath chemin de la nouvelle photo de profil
     * @return utilisateur mis à jour
     */
    @Transactional
    public User updateProfilePicture(UUID userId, String profilePicturePath) {
        User user = getUserById(userId);

        // Supprimer l'ancienne photo si elle existe
        if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
            try {
                fileStorageService.deleteFile(user.getProfilePicture());
            } catch (Exception e) {
                log.warn("Failed to delete old profile picture: {}", e.getMessage());
            }
        }

        user.setProfilePicture(profilePicturePath);
        User updated = userRepository.save(user);
        log.info("Profile picture updated for user {}", user.getEmail());

        return updated;
    }

    /**
     * Supprime la photo de profil d'un utilisateur.
     *
     * @param userId identifiant de l'utilisateur
     * @return utilisateur mis à jour
     */
    @Transactional
    public User deleteProfilePicture(UUID userId) {
        User user = getUserById(userId);

        if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
            try {
                fileStorageService.deleteFile(user.getProfilePicture());
            } catch (Exception e) {
                log.warn("Failed to delete profile picture: {}", e.getMessage());
            }

            user.setProfilePicture(null);
            User updated = userRepository.save(user);
            log.info("Profile picture deleted for user {}", user.getEmail());

            return updated;
        }

        return user;
    }
}
