package com.hairbook.service.impl;

import com.hairbook.dto.auth.AuthRequest;
import com.hairbook.dto.auth.AuthResponse;
import com.hairbook.entity.ERole;
import com.hairbook.entity.Role;
import com.hairbook.entity.User;
import com.hairbook.repository.UserRepository;
import com.hairbook.repository.RoleRepository;
import com.hairbook.security.JwtTokenProvider;
import com.hairbook.security.LoginAttemptService;
import com.hairbook.service.RefreshTokenService;
import com.hairbook.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation du service de gestion des utilisateurs.
 * Gère l'authentification, l'inscription, et les opérations CRUD pour les
 * utilisateurs.
 * Intègre la gestion des tokens JWT, la protection contre les attaques par
 * force brute et la mise en cache.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final LoginAttemptService loginAttemptService;

    /**
     * Constructeur pour l'injection des dépendances.
     *
     * @param userRepository        Repository pour les utilisateurs.
     * @param roleRepository        Repository pour les rôles.
     * @param refreshTokenService   Service pour les jetons de rafraîchissement.
     * @param passwordEncoder       Encodeur pour les mots de passe.
     * @param jwtTokenProvider      Fournisseur de jetons JWT.
     * @param authenticationManager Gestionnaire d'authentification de Spring
     *                              Security.
     * @param loginAttemptService   Service de suivi des tentatives de connexion.
     */
    public UserServiceImpl(UserRepository userRepository,
            RoleRepository roleRepository,
            RefreshTokenService refreshTokenService,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            AuthenticationManager authenticationManager,
            LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.loginAttemptService = loginAttemptService;
    }

    // ----------------- Auth -----------------
    /**
     * Authentifie un utilisateur et retourne les jetons d'accès et de
     * rafraîchissement.
     * Inclut une protection contre les attaques par force brute basée sur l'adresse
     * IP.
     *
     * @param request Les informations d'authentification (email, mot de passe).
     * @return Une réponse contenant les jetons et l'ID de l'utilisateur.
     * @throws IllegalArgumentException si les identifiants sont invalides ou si le
     *                                  compte est bloqué.
     */
    @Override
    public AuthResponse authenticate(AuthRequest request) {
        String clientIp = getClientIpAddress();

        if (loginAttemptService.isBlocked(clientIp)) {
            long remainingMinutes = loginAttemptService.getRemainingLockoutMinutes(clientIp);
            throw new IllegalArgumentException(
                    String.format("Trop de tentatives de connexion échouées. Compte bloqué pendant %d minutes.",
                            remainingMinutes));
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

            loginAttemptService.recordSuccessfulAttempt(clientIp);

            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);

            String accessToken = jwtTokenProvider.generateToken(authentication);
            String refreshToken = refreshTokenService.create(user.getId()).getToken();

            return new AuthResponse(accessToken, refreshToken, user.getId());
        } catch (BadCredentialsException e) {
            loginAttemptService.recordFailedAttempt(clientIp);

            int remainingAttempts = loginAttemptService.getRemainingAttempts(clientIp);
            if (remainingAttempts > 0) {
                throw new IllegalArgumentException(
                        String.format("Identifiants invalides. %d tentatives restantes.", remainingAttempts));
            } else {
                throw new IllegalArgumentException("Trop de tentatives échouées. Compte temporairement bloqué.");
            }
        } catch (Exception e) {
            loginAttemptService.recordFailedAttempt(clientIp);
            throw new IllegalArgumentException("Erreur d'authentification");
        }
    }

    /**
     * Récupère l'adresse IP du client
     */
    private String getClientIpAddress() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            // Vérifier les headers de proxy
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                return xForwardedFor.split(",")[0].trim();
            }

            String xRealIp = request.getHeader("X-Real-IP");
            if (xRealIp != null && !xRealIp.isEmpty()) {
                return xRealIp;
            }

            return request.getRemoteAddr();
        }

        return "unknown";
    }

    /**
     * Enregistre un nouvel utilisateur avec le rôle par défaut (ROLE_MEMBER).
     * Le mot de passe est haché avant d'être sauvegardé.
     *
     * @param user L'utilisateur à enregistrer.
     * @return L'utilisateur sauvegardé.
     * @throws IllegalArgumentException si l'email est déjà utilisé.
     */
    @Override
    public User register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email déjà utilisé");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role memberRole = roleRepository.findByName(ERole.ROLE_MEMBER)
                .orElseThrow(() -> new IllegalStateException("Rôle ROLE_MEMBER non trouvé dans la base de données"));
        user.getRoles().add(memberRole);

        return userRepository.save(user);
    }

    // ----------------- CRUD -----------------
    /**
     * Crée un nouvel utilisateur (généralement par un administrateur).
     * Le mot de passe est haché s'il est fourni.
     * Invalide le cache des utilisateurs.
     *
     * @param user L'utilisateur à créer.
     * @return L'utilisateur sauvegardé.
     */
    @Override
    @CacheEvict(value = "users", allEntries = true)
    public User create(User user) {
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    @Override
    @Cacheable(value = "users", key = "#id")
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Cacheable(value = "users", key = "#email")
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Cacheable(value = "users", key = "'all'")
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Met à jour un utilisateur existant.
     * Si un nouveau mot de passe est fourni, il est haché. Sinon, l'ancien est
     * conservé.
     * Invalide le cache des utilisateurs.
     *
     * @param user L'utilisateur avec les informations mises à jour.
     * @return L'utilisateur mis à jour.
     */
    @Override
    @CacheEvict(value = "users", allEntries = true)
    public User update(User user) {
        // Si un nouveau mot de passe est fourni, le hasher avec BCrypt
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            userRepository.findById(user.getId())
                    .ifPresent(existingUser -> user.setPassword(existingUser.getPassword()));
        }
        return userRepository.save(user);
    }

    @Override
    @CacheEvict(value = "users", allEntries = true)
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Récupère l'utilisateur actuellement authentifié à partir d'un token JWT.
     *
     * @param token Le token JWT (sans le préfixe 'Bearer ').
     * @return L'utilisateur correspondant au token.
     * @throws IllegalArgumentException si aucun utilisateur n'est trouvé pour ce
     *                                  token.
     */
    @Override
    public User getCurrentUserFromToken(String token) {
        String email = jwtTokenProvider.getUsernameFromToken(token);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable pour le token fourni"));
    }
}
