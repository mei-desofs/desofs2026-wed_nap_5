package com.grupo.learningmore.security;

import com.grupo.learningmore.api.UserController;
import com.grupo.learningmore.domain.user.User;
import com.grupo.learningmore.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final JwtService jwtService;
    private final UserService userService;

    public JwtFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        log.info("Incoming request: {} {}", request.getMethod(), request.getServletPath());

        if (request.getServletPath().startsWith("/api/auth")) {
            log.info("Skipping JWT filter for /api/auth");
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        if (header == null) {
            log.warn("Missing Authorization header");
            filterChain.doFilter(request, response);
            return;
        }

        if (!header.startsWith("Bearer ")) {
            log.warn("Invalid Authorization format: {}", header);
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        log.info("JWT token received");

        if (!jwtService.isTokenValid(token)) {
            log.warn("Invalid JWT token");
            filterChain.doFilter(request, response);
            return;
        }

        String username = jwtService.extractUsername(token);
        String role = jwtService.extractRole(token);
        Long tokenVersion = jwtService.extractTokenVersion(token);

        log.info("JWT claims -> username: {}, role: {}",
                username, role);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {

                log.info("Loading user from DB: {}", username);

                User user = userService.findById(username);

                log.info("User found -> active: {}",
                        user.isActive());

                boolean invalidUser =
                        !user.isActive()
                                || tokenVersion == null
                                || !tokenVersion.equals(user.getTokenVersion());

                if (invalidUser) {
                    log.warn("Invalid user or token version mismatch");
                    filterChain.doFilter(request, response);
                    return;
                }

                String cleanRole = role == null ? "" : role.trim().toUpperCase();

                log.info("Creating authority: ROLE_{}", cleanRole);

                var authority = new SimpleGrantedAuthority("ROLE_" + cleanRole);

                var auth = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        List.of(authority)
                );

                auth.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(auth);

                log.info("Authentication set successfully for user {}", username);

            } catch (IllegalArgumentException e) {

                log.error("Invalid String in token: {}", username);
                filterChain.doFilter(request, response);
                return;
            }
        }

        log.info("Continuing filter chain for {}", request.getServletPath());
        filterChain.doFilter(request, response);
    }
}
