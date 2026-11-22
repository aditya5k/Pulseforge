package com.aditya.pulseforge.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 1. Read the Authorization header
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // No Bearer token? Skip, and let Spring Security decide later
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Remove "Bearer " to get the raw token
        String token = authHeader.substring(BEARER_PREFIX.length());

        try {
            // 3. Read the email from the token (throws if tampered/expired)
            String email = jwtService.extractUsername(token);

            // Only authenticate if not already authenticated in this request
            if (email != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 4. Load the user from DB
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // 5. Validate the token, then mark the request as authenticated
                if (jwtService.isTokenValid(token, userDetails)) {

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,                          // no password needed, token already proved identity
                                    userDetails.getAuthorities()   // roles, for RBAC later
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (JwtException | IllegalArgumentException | UsernameNotFoundException ex) {
            // Invalid/expired token, or user deleted: stay unauthenticated, so 401.
            // Never log the token itself.
        }

        // 6. Always continue the chain
        filterChain.doFilter(request, response);
    }
}