package com.hairbook.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Service d'authentification gérant l'inscription, la connexion et la récupération d'utilisateur courant.
 * <p>
 * Responsabilités :
 * <ul>
 *   <li>Créer un compte client par défaut et encoder le mot de passe</li>
 *   <li>Authentifier un utilisateur et émettre un JWT</li>
 *   <li>Fournir l'utilisateur courant par email</li>
 * </ul>
 * Les erreurs fonctionnelles sont levées en {@link IllegalArgumentException}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final AuditService auditService;
    private final EmailService emailService;
    private final BlacklistService blacklistService;

    /**
     * Enregistre un nouvel utilisateur avec le rôle {@code ROLE_CLIENT} et renvoie un JWT.
     *
     * @param request données d'inscription (email, mot de passe, prénom, nom, téléphone)
     * @return réponse d'authentification contenant le token et les infos essentielles
     * @throws IllegalArgumentException si un compte existe déjà avec cet email
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("An account with this email already exists");
        }

        // Vérifier si l'email ou le téléphone est dans la blacklist
        String blacklistError = blacklistService.checkBlacklist(request.getEmail(), request.getPhone());
        if (blacklistError != null) {
            throw new IllegalArgumentException(blacklistError);
        }

        Set<String> roles = new HashSet<>();
        roles.add("ROLE_CLIENT");

        // Générer un token de vérification unique
        String verificationToken = UUID.randomUUID().toString();
        LocalDateTime tokenExpiry = LocalDateTime.now().plusHours(24); // Valide 24h

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .roles(roles)
                .emailVerified(false)
                .active(true)
                .emailVerificationToken(verificationToken)
                .emailVerificationTokenExpiresAt(tokenExpiry)
                .build();

        user = userRepository.save(user);
        log.info("New user registered: {}", user.getEmail());

        // Envoyer l'email de bienvenue avec lien de vérification
        emailService.sendWelcomeEmail(user.getEmail(), user.getFirstName(), verificationToken);

        // Audit log
        auditService.logAction(user.getEmail(), "USER_REGISTERED", "User", user.getId(),
                java.util.Map.of("email", user.getEmail()));

        String token = jwtUtil.generateToken(user.getEmail(), new ArrayList<>(user.getRoles()));

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .roles(new ArrayList<>(user.getRoles()))
                .build();
    }

    /**
     * Authentifie un utilisateur via email/mot de passe et émet un JWT.
     *
     * @param request email et mot de passe
     * @return réponse d'authentification avec token JWT
     * @throws IllegalArgumentException si l'utilisateur est introuvable ou désactivé
     *                                  (les identifiants invalides sont gérés par l'AuthenticationManager)
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Variable pour stocker le message de bienvenue si nécessaire
        String welcomeMessage = null;

        // Vérifier si le compte est inactif
        if (!user.getActive()) {
            // Vérifier si c'est une suppression programmée par le client
            if (user.getDeletionScheduledAt() != null) {
                // Annuler la suppression programmée et réactiver le compte
                user.setDeletionScheduledAt(null);
                user.setActive(true);
                userRepository.save(user);
                log.info("Account deletion cancelled for user {} upon login", user.getEmail());

                // Message de bienvenue pour l'utilisateur qui revient
                welcomeMessage = "Content de vous revoir ! Votre demande de suppression de compte a été annulée.";

                // Audit log pour l'annulation de la suppression
                auditService.logAction(user.getEmail(), "DELETION_CANCELLED", "User", user.getId(),
                        java.util.Map.of("reason", "User logged in before deletion date"));
            } else {
                // Le compte a été bloqué par un administrateur
                throw new IllegalArgumentException("Votre compte a été bloqué par un administrateur. Vous ne pouvez plus accéder au système. Veuillez contacter le support pour plus d'informations.");
            }
        }

        String token = jwtUtil.generateToken(user.getEmail(), new ArrayList<>(user.getRoles()));
        log.info("User logged in: {}", user.getEmail());

        // Audit log
        auditService.logAction(user.getEmail(), "USER_LOGIN", "User", user.getId(), null);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .roles(new ArrayList<>(user.getRoles()))
                .message(welcomeMessage)
                .build();
    }

    /**
     * Récupère l'utilisateur courant par son email.
     *
     * @param email email de l'utilisateur
     * @return entité {@link User}
     * @throws IllegalArgumentException si l'utilisateur n'existe pas
     */
    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    /**
     * Valide un token JWT.
     * Utilise la méthode isTokenValid() de JwtUtil.
     *
     * @param token token JWT à valider
     * @return true si le token est valide et non expiré
     */
    public boolean validateToken(String token) {
        return jwtUtil.isTokenValid(token);
    }

    /**
     * Rafraîchit un token JWT si celui-ci est encore valide.
     * Génère un nouveau token avec les mêmes informations.
     *
     * @param token token JWT actuel
     * @return nouveau token JWT
     * @throws IllegalArgumentException si le token est invalide
     */
    public String refreshToken(String token) {
        if (!jwtUtil.isTokenValid(token)) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        String email = jwtUtil.extractEmail(token);
        List<String> roles = jwtUtil.extractRoles(token);

        String newToken = jwtUtil.generateToken(email, roles);
        log.info("Token refreshed for user: {}", email);
        return newToken;
    }
}
