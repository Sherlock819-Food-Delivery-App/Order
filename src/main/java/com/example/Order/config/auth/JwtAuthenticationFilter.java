package com.example.Order.config.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Check if the user is already authenticated
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            // User is already authenticated, proceed to the next filter in the chain
            filterChain.doFilter(request, response);
            return; // Exit the filter chain
        }

        String token = getJwtFromRequest(request);

        log.info("Token: {}", token);
        if (token != null && jwtTokenProvider.validateToken(token)) {
            log.info("Token is valid");
            String email = jwtTokenProvider.getEmailFromToken(token);

            // Load the user details from the database
            // Why are we not pulling userDetails from DB, what if token is valid but email in it is not in DB???
            List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("USER");
            UserDetails userDetails = new User(email, "", authorities);

            log.info("Setting authentication for user: {}", email);

            // Create an authentication token using the UserDetails
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        bearerToken = request.getQueryString();
        if(bearerToken != null && bearerToken.startsWith("token=")) {
            return bearerToken.substring(6).split("&")[0];
        }
        return null;
    }
}

