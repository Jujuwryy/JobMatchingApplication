package com.george.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.george.Security.User;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service class for handling JSON Web Token (JWT) operations including token generation,
 * validation, and claim extraction.
 * 
 * This service implements JWT functionality using the HMAC-SHA256 algorithm for signing tokens.
 * It automatically generates a secure key on initialization and provides methods for the complete
 * lifecycle management of JWTs.
 * 
 * The tokens generated include:
 * - Custom claims (if provided)
 * - Subject (username)
 * - Issued at timestamp
 * - Expiration (set to 30 hours from issuance)
 */
@Service
public class JWTService {

    /** The base64 encoded secret key used for signing JWTs */
    private String secretkey = "";

    /**
     * Initializes the JWTService by generating a secure HMAC-SHA256 key.
     * The key is stored in base64 encoded format.
     * 
     * @throws RuntimeException if the HMAC-SHA256 algorithm is not available
     */
    public JWTService() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGen.generateKey();
            secretkey = Base64.getEncoder().encodeToString(sk.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a JWT token for the specified username.
     * The token includes an empty claims map, username as subject, issuance time,
     * and expiration time (30 hours from issuance).
     * 
     * @param username the username to be included in the token
     * @return the generated JWT token string
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 30))
                .and()
                .signWith(getKey())
                .compact();
    }

    /**
     * Retrieves the signing key used for JWT operations.
     * Converts the base64 encoded secret key back to a SecretKey object.
     * 
     * @return SecretKey object used for signing JWTs
     */
    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretkey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extracts the username from a JWT token.
     * 
     * @param token the JWT token string
     * @return the username stored in the token's subject claim
     */
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Generic method to extract a specific claim from a JWT token.
     * 
     * @param <T> the type of the claim to extract
     * @param token the JWT token string
     * @param claimResolver function to extract the desired claim from the Claims object
     * @return the extracted claim value
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    /**
     * Extracts all claims from a JWT token.
     * Verifies the token's signature using the secret key.
     * 
     * @param token the JWT token string
     * @return Claims object containing all claims from the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Validates a JWT token against a UserDetails object.
     * Checks if the username in the token matches the UserDetails
     * and if the token hasn't expired.
     * 
     * @param token the JWT token string to validate
     * @param userDetails the UserDetails object to validate against
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Checks if a JWT token has expired.
     * 
     * @param token the JWT token string to check
     * @return true if the token has expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from a JWT token.
     * 
     * @param token the JWT token string
     * @return Date object representing the token's expiration time
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}