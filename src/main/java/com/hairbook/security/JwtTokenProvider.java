package com.hairbook.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import com.hairbook.config.JwtProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Fournit des méthodes pour générer, valider et extraire des informations des
 * tokens JWT.
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final SecretKey secretKey;
    private final JwtProperties jwtProperties;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        byte[] keyBytes = Decoders.BASE64.decode(this.jwtProperties.getSecret());
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Génère la clé secrète utilisée pour signer et vérifier les tokens JWT.
     * La clé est dérivée de la propriété 'app.jwt.secret' qui doit être encodée en
     * Base64.
     * 
     * @return La SecretKey pour les opérations JWT.
     */
    private SecretKey getSigningKey() {
        return secretKey;
    }

    /**
     * Génère un token JWT pour un utilisateur authentifié.
     * 
     * @param authentication L'objet d'authentification contenant les détails de
     *                       l'utilisateur.
     * @return Une chaîne représentant le token JWT.
     */
    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpirationMs());

        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .claim("userId", userPrincipal.getId())
                .claim("roles", userPrincipal.getAuthorities().stream()
                        .map(a -> a.getAuthority()).toArray(String[]::new))
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    /**
     * Génère un token JWT à partir d'un nom d'utilisateur.
     * Utile pour des scénarios comme le rafraîchissement de token.
     * 
     * @param username Le nom d'utilisateur.
     * @return Une chaîne représentant le token JWT.
     */
    public String generateTokenFromUsername(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpirationMs());

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    /**
     * Extrait le nom d'utilisateur (le 'subject') d'un token JWT.
     * 
     * @param token Le token JWT.
     * @return Le nom d'utilisateur.
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    /**
     * Valide un token JWT.
     * Vérifie la signature, l'expiration et le format du token.
     * 
     * @param authToken Le token à valider.
     * @return true si le token est valide, sinon false.
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException ex) {
            logger.error("Signature JWT invalide");
        } catch (MalformedJwtException ex) {
            logger.error("Token JWT malformé");
        } catch (ExpiredJwtException ex) {
            logger.error("Token JWT expiré");
        } catch (UnsupportedJwtException ex) {
            logger.error("Token JWT non supporté");
        } catch (IllegalArgumentException ex) {
            logger.error("Claims JWT vide");
        }
        return false;
    }

    /**
     * Récupère la date d'expiration d'un token JWT.
     * 
     * @param token Le token JWT.
     * @return La date d'expiration.
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getExpiration();
    }

    /**
     * Vérifie si un token JWT a expiré.
     * 
     * @param token Le token JWT.
     * @return true si le token est expiré, sinon false.
     */
    public boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    /**
     * Extrait l'ID de l'utilisateur du corps (claims) d'un token JWT.
     * 
     * @param token Le token JWT.
     * @return L'ID de l'utilisateur.
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("userId", Long.class);
    }

    /**
     * Extrait les rôles de l'utilisateur du corps (claims) d'un token JWT.
     * 
     * @param token Le token JWT.
     * @return Un tableau de chaînes représentant les rôles.
     */
    public String[] getRolesFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("roles", String[].class);
    }
}
