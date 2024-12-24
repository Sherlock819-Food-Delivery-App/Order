package com.example.Order.config.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Component
public class JwtTokenProvider {

    @Value("${JWT_SECRET}")
    private String jwtSecret; // Store securely in properties
    private final long jwtExpirationMs = 3600000; // 1 hour validity
    private final long refreshTokenExpirationMs = 604800000; // 7 days validity for refresh token

    private static final String PROPERTIES_FILE_PATH = "src/main/resources/application.properties";

    @PostConstruct
    public void init() {
        // Check if the JWT_SECRET is empty
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            // Generate a new secret key
            try {
                KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
                SecretKey sk = keyGen.generateKey();
                jwtSecret = Base64.getEncoder().encodeToString(sk.getEncoded());

                // Save the generated key to application.properties
                saveSecretToProperties(jwtSecret);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void saveSecretToProperties(String secret) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(PROPERTIES_FILE_PATH));
            FileWriter writer = new FileWriter(PROPERTIES_FILE_PATH);

            for (String line : lines) {
                // Update the JWT_SECRET line or append if not exists
                if (line.startsWith("JWT_SECRET=")) {
                    line = "JWT_SECRET=" + secret;
                }
                writer.write(line + System.lineSeparator());
            }

            // If the line for JWT_SECRET does not exist, append it
            writer.write("JWT_SECRET=" + secret + System.lineSeparator());

            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to save JWT_SECRET to properties file", e);
        }
    }

    public String generateToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .and()
                .signWith(getKey())
                .compact();
    }

    public String generateRefreshToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMs))
                .and()
                .signWith(getKey())
                .compact();
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return !claims.getExpiration().before(new Date()); // Check if the token is expired
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Invalid JWT token");
        }
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token);
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
