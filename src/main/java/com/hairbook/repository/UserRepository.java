package com.hairbook.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hairbook.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour gérer les opérations de base de données pour l'entité User.
 * Fournit des méthodes pour rechercher, sauvegarder, et supprimer des utilisateurs.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Recherche un utilisateur par son adresse e-mail.
     * @param email L'adresse e-mail à rechercher.
     * @return un Optional contenant l'utilisateur s'il est trouvé, sinon un Optional vide.
     */
    Optional<User> findByEmail(String email);

    /**
     * Recherche un utilisateur par son numéro de téléphone.
     * @param phone Le numéro de téléphone à rechercher.
     * @return un Optional contenant l'utilisateur s'il est trouvé, sinon un Optional vide.
     */
    Optional<User> findByPhone(String phone);

    /**
     * Recherche des utilisateurs dont le numéro de téléphone commence par un préfixe donné.
     * @param phonePrefix Le préfixe du numéro de téléphone.
     * @return Une liste d'utilisateurs correspondants.
     */
    @Query("SELECT u FROM User u WHERE u.phone LIKE :phonePrefix%")
    List<User> findByPhoneStartingWith(@Param("phonePrefix") String phonePrefix);

    /**
     * Vérifie si un utilisateur existe avec l'adresse e-mail donnée.
     * @param email L'adresse e-mail à vérifier.
     * @return true si un utilisateur avec cet e-mail existe, sinon false.
     */
    boolean existsByEmail(String email);

    /**
     * Vérifie si un utilisateur existe avec le numéro de téléphone donné.
     * @param phone Le numéro de téléphone à vérifier.
     * @return true si un utilisateur avec ce numéro existe, sinon false.
     */
    boolean existsByPhone(String phone);

    /**
     * Trouve tous les utilisateurs créés après une date spécifique.
     * @param date La date de référence.
     * @return Une liste d'utilisateurs.
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Trouve les utilisateurs considérés comme inactifs depuis une date donnée.
     * Un utilisateur est inactif si sa dernière connexion est antérieure à la date de référence ou s'il ne s'est jamais connecté.
     * @param cutoffDate La date limite pour considérer un utilisateur comme inactif.
     * @return Une liste d'utilisateurs inactifs.
     */
    @Query("SELECT u FROM User u WHERE u.lastLoginAt < :cutoffDate OR u.lastLoginAt IS NULL")
    List<User> findInactiveUsersSince(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Compte le nombre d'utilisateurs ayant un numéro de téléphone belge.
     * @return Le nombre total d'utilisateurs belges.
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.phone LIKE '+32%' OR u.phone LIKE '0%'")
    long countBelgianUsers();
}
