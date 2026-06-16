package com.grupo.learningmore.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

/**
 * Filter to validate Sec-Fetch-* headers (Fetch Metadata) to protect against
 * cross-origin resource leakage and CSRF.
 */
@Component
public class FetchMetadataFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(FetchMetadataFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String secFetchSite = request.getHeader("Sec-Fetch-Site");

        // If the browser doesn't support Sec-Fetch headers, allow the request (fail-open)
        if (secFetchSite == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Allow same-origin, same-site and none (e.g. direct navigation)
        if ("same-origin".equals(secFetchSite) || "same-site".equals(secFetchSite) || "none".equals(secFetchSite)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Cross-site request
        String secFetchMode = request.getHeader("Sec-Fetch-Mode");
        String secFetchDest = request.getHeader("Sec-Fetch-Dest");

        // Allow top-level navigations from other sites
        if ("navigate".equals(secFetchMode) && "GET".equalsIgnoreCase(request.getMethod()) &&
                Set.of("document", "nested-document").contains(secFetchDest)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Allow CORS requests (these will be validated by the CORS policy if configured)
        if ("cors".equals(secFetchMode)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Block all other cross-site requests (e.g. no-cors embedding attempts)
        log.warn("Blocking cross-site request: Sec-Fetch-Site={}, Sec-Fetch-Mode={}, Sec-Fetch-Dest={}, Path={}",
                secFetchSite, secFetchMode, secFetchDest, request.getServletPath());
        
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("text/plain");
        response.getWriter().write("Forbidden: Cross-site request blocked by Fetch Metadata policy.");
    }
}
