package com.project.store_management_tool.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

public class JWTUtil {
    public String generateToken(String email, String role) {
        return Jwts.builder().setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractallClaims(token);
        return claimsResolvers.apply(claims);
    }

    private Claims extractallClaims(String token) {
        return Jwts.parser().setSigningKey(getSignKey())
                .build().parseClaimsJws(token).getBody();
    }

    public boolean isTokenValid(String token, String email, String role) {
        final String extractedEmail = extractEmail(token);
        final String extractedRole = extractRole(token);
        return (extractedEmail.equals(email) && !isTokenExpired(token) && extractedRole.equals(role));
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private Key getSignKey() {
        byte[] key = Decoders.BASE64.decode("413F4428472B4B6250655368566D5970337336763979244226452948404D6351");
        return Keys.hmacShaKeyFor(key);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
}
