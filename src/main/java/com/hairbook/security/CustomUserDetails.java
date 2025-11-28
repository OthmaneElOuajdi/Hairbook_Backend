package com.hairbook.security;


import com.hairbook.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Implémentation personnalisée de {@link UserDetails} pour l’intégration
 * de l’entité {@link User} avec Spring Security.
 *
 * <p>Cette implémentation est désormais un {@code record}, ce qui la rend
 * immuable, concise et adaptée aux besoins d’un simple conteneur de données
 * pour l’utilisateur connecté.</p>
 *
 * <p>Elle adapte le modèle métier {@code User} au format attendu
 * par le framework d’authentification, en exposant notamment :
 * <ul>
 *   <li>L'adresse e-mail comme nom d’utilisateur ;</li>
 *   <li>le mot de passe hashé ;</li>
 *   <li>Les rôles convertis en {@link SimpleGrantedAuthority} ;</li>
 *   <li>L'état actif/désactivé du compte.</li>
 * </ul>
 *
 * <p>Elle est utilisée par {@link CustomUserDetailsService} lors du chargement
 * de l’utilisateur dans la méthode {@code loadUserByUsername(...)}.</p>
 */
public record CustomUserDetails(User user) implements UserDetails {

    /**
     * Retourne les autorités (rôles) de l’utilisateur sous forme de {@link GrantedAuthority}.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * Retourne le mot de passe hashé stocké en base.
     */
    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    /**
     * Retourne l’e-mail de l’utilisateur (identifiant de connexion).
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Le compte n’expire jamais (par défaut).
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Retourne {@code true} si le compte n'est pas verrouillé.
     * <p>Un compte inactif est considéré comme "verrouillé", SAUF s'il a une suppression programmée
     * (dans ce cas, on permet la connexion pour que l'utilisateur puisse annuler la suppression).</p>
     */
    @Override
    public boolean isAccountNonLocked() {
        // Si le compte est actif, il n'est pas verrouillé
        if (Boolean.TRUE.equals(user.getActive())) {
            return true;
        }

        // Si le compte est inactif mais a une suppression programmée,
        // on permet la connexion (la logique de réactivation se fera dans AuthService.login)
        if (user.getDeletionScheduledAt() != null) {
            return true;
        }

        // Sinon, le compte a été bloqué par un admin
        return false;
    }

    /**
     * Les identifiants ne sont jamais expirés (par défaut).
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Retourne {@code true} si l'utilisateur est actif.
     * <p>Un compte inactif est considéré comme "désactivé", SAUF s'il a une suppression programmée
     * (dans ce cas, on permet la connexion pour que l'utilisateur puisse annuler la suppression).</p>
     */
    @Override
    public boolean isEnabled() {
        // Si le compte est actif, il est enabled
        if (Boolean.TRUE.equals(user.getActive())) {
            return true;
        }

        // Si le compte est inactif mais a une suppression programmée,
        // on permet la connexion (la logique de réactivation se fera dans AuthService.login)
        if (user.getDeletionScheduledAt() != null) {
            return true;
        }

        // Sinon, le compte a été désactivé/bloqué par un admin
        return false;
    }
}
