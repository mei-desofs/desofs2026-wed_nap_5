package com.grupo.learningmore.security;

import com.grupo.learningmore.domain.user.User;
import com.grupo.learningmore.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFilter extends OncePerRequestFilter {

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

        if (request.getServletPath().startsWith("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        if (!jwtService.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        String username = jwtService.extractUsername(token);
        String role = jwtService.extractRole(token);
        Long tokenVersion = jwtService.extractTokenVersion(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {

                User user = userService.findById(UUID.fromString(username));

                boolean invalidUser =
                        !user.isActive()
                                || tokenVersion == null
                                || tokenVersion != user.getTokenVersion();

                if (invalidUser) {
                    filterChain.doFilter(request, response);
                    return;
                }

                var authority = new SimpleGrantedAuthority("ROLE_" + role);

                var auth = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        List.of(authority)
                );

                auth.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (IllegalArgumentException e) {

                filterChain.doFilter(request, response);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
