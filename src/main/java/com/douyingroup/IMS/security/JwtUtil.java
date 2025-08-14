package com.douyingroup.IMS.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Component
public class JwtUtil {

    private final KeyPair keyPair;          // injected from RsaKeyConfig

    @Value("${jwt.expiration-minutes}")
    private long expirationMinutes;

    /* ------------------------------------------------------------------
     *  New canonical method: username + authorities (preferred)
     * ---------------------------------------------------------------- */
    public String generate(String username,
                           Collection<? extends GrantedAuthority> authorities) {

        return buildToken(username,
                null,   // uid
                authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList());
    }

    /* ------------------------------------------------------------------
     *  OVERLOAD for legacy call in AuthService
     * ---------------------------------------------------------------- */
    public String generateToken(String username, String userId, String role) {
        return buildToken(username, userId, List.of("ROLE_" + role));
    }

    /* ------------------------------------------------------------------
     *  Shared builder
     * ---------------------------------------------------------------- */
    private String buildToken(String username,
                              String userId,
                              List<String> roles) {

        Date now = new Date();
        Date exp = new Date(now.getTime()
                + Duration.ofMinutes(expirationMinutes).toMillis());

        return Jwts.builder()
                .setSubject(username)
                .claim("uid",   userId)  // include userId for downstream use
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                .compact();
    }

    /* ------------------------------------------------------------------
     *  Validate & parse
     * ---------------------------------------------------------------- */
    public Claims validate(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(keyPair.getPublic())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
