package com.douyingroup.IMS.security;   // ← change if your base package differs

import com.douyingroup.IMS.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil     jwtUtil;
    private final UserService userService;   // to load UserDetails if you need it

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = extractToken(request);

        if (StringUtils.hasText(token)) {
            try {
                /* ----- RS256 signature + claims validation ----- */
                Claims claims   = jwtUtil.validate(token);   // throws JwtException if bad
                String username = claims.getSubject();
                String uid      = claims.get("uid", String.class);
                @SuppressWarnings("unchecked")
                List<String> roleCodes = claims.get("roles", List.class);  // may be null

                if (username != null
                        && SecurityContextHolder.getContext().getAuthentication() == null) {

                    /* (Optional) load the full UserDetails from DB */
                    UserDetails userDetails = userService.loadUserByUsername(username);

                    /* Build authorities list */
                    List<SimpleGrantedAuthority> authorities = roleCodes == null
                            ? Collections.emptyList()
                            : roleCodes.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,    // principal
                                    null,           // credentials (null = already verified)
                                    authorities);

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));

                    /* Put the auth object into SecurityContext */
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    /* Expose logging fields */
                    String resolvedUserId = uid;
                    if (resolvedUserId == null && userDetails instanceof AuthenticatedUser au) {
                        resolvedUserId = au.getUserId();
                    }
                    request.setAttribute("userId", resolvedUserId);
                    request.setAttribute("username", username);
                }

            } catch (JwtException ex) {
                log.warn("Invalid or expired JWT: {}", ex.getMessage());
                // No auth set → will fall through; downstream may return 401
            } catch (Exception ex) {
                log.error("Cannot set user authentication", ex);
            }
        }

        filterChain.doFilter(request, response);
    }

    /* -------------------------------- helpers ------------------------------- */

    private String extractToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
