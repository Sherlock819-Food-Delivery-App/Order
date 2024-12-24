package com.example.Order.config.websocket;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
public class JwtHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    private static final Logger log = LoggerFactory.getLogger(JwtHandshakeInterceptor.class);
    @Value("${JWT_SECRET}")
    private String jwtSecret;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {

        // Extract JWT from the headers
        log.info("Request Headers: {}", request.getHeaders());

        String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if(authorizationHeader == null)
        {
            String token = request.getURI().getQuery(); // Get the query string
            String jwtToken = null;
            if (token != null && token.startsWith("token=")) {
                authorizationHeader = token.split("=")[1]; // Extract the token value
                authorizationHeader = "Bearer " + authorizationHeader.split("&")[0];
            }
        }

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwtToken = authorizationHeader.substring(7); // Extract token part after "Bearer "

            // Validate the JWT token (implement your own JWT validation logic)
            if (isValidJwt(jwtToken)) {
                // Extract user ID or any relevant details from the JWT
                String userId = getUserIdFromJwt(jwtToken);

                // Store user ID in WebSocket session attributes
                attributes.put("userId", userId);
                return true; // Allow the handshake
            }
        }

        // If JWT is invalid, deny the handshake
        return false;
    }

    // Helper methods for JWT validation and extracting user ID
    private boolean isValidJwt(String token) {
        // Implement JWT validation logic (e.g., using a library like jjwt or Auth0)
        // Return true if valid, false otherwise
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

    private String getUserIdFromJwt(String token) {
        // Extract the user ID from the JWT (implement according to your JWT structure)
        Claims claims = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

