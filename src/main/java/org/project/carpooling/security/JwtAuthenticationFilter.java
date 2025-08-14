package org.project.carpooling.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
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

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService,
                                   CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Gestion des requêtes OPTIONS pour CORS
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Ignorer les endpoints publics
        if (request.getServletPath().startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 3. Extraction du token
            final String authHeader = request.getHeader(AUTH_HEADER);

            if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
                logger.warn("JWT Token is missing or invalid format");
                filterChain.doFilter(request, response);
                return;
            }

            final String token = authHeader.substring(BEARER_PREFIX.length());

            // 4. Validation du token
            final String username = jwtService.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(token, userDetails)) {
                    // 5. Création de l'authentication token
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // 6. Mise à jour du contexte de sécurité
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("Authenticated user: {}", username);
                }
            }
        } catch (UsernameNotFoundException ex) {
            logger.error("User not found in database", ex);
        } catch (Exception ex) {
            logger.error("Authentication error", ex);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 7. Continuer la chaîne de filtres
        filterChain.doFilter(request, response);
    }
}