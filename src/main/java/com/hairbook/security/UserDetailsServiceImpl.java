package com.hairbook.security;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hairbook.entity.User;
import com.hairbook.repository.UserRepository;

/**
 * Implémentation du service UserDetailsService de Spring Security.
 * Ce service est responsable de charger les détails de l'utilisateur (par email ou ID) depuis la base de données.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Charge un utilisateur par son adresse e-mail.
     * C'est la méthode principale utilisée par Spring Security lors de l'authentification.
     *
     * @param email L'adresse e-mail de l'utilisateur à charger.
     * @return Un objet UserDetails contenant les informations de l'utilisateur.
     * @throws UsernameNotFoundException si aucun utilisateur n'est trouvé avec cet e-mail.
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email));

        return UserPrincipal.create(user);
    }

    /**
     * Charge un utilisateur par son identifiant unique.
     * Utile pour des opérations internes, comme la validation de token JWT.
     *
     * @param id L'ID de l'utilisateur à charger.
     * @return Un objet UserDetails contenant les informations de l'utilisateur.
     * @throws UsernameNotFoundException si aucun utilisateur n'est trouvé avec cet ID.
     */
    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'ID: " + id));

        return UserPrincipal.create(user);
    }
}

