package com.example.jwtoken.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.key}")
    private String SECRET;
    private static final long ACCESS_TOKEN_EXPIRATION = 15 * 60 * 60 * 1000L; // 30 seconds
    private static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000L; //


    public String generateAccessToken(UserDetails user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tokenType", "access");
        return createToken(claims, user.getUsername(), ACCESS_TOKEN_EXPIRATION);
    }

    public String generateRefreshToken(UserDetails user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tokenType", "refresh");
        return createToken(claims, user.getUsername(), REFRESH_TOKEN_EXPIRATION);
    }

    private String createToken(Map<String, ?> claims, String subject, long validityMillis) {
        Date now = new Date();
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + validityMillis))
                .signWith(getSignKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignKey()) // HMAC key
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject(); // 'sub' claim = username
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        Date expiration = extractExpiration(token);

        return userDetails.getUsername().equals(username) && expiration.after(new Date());
    }

    private Date extractExpiration(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignKey()) // HMAC key
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getExpiration();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
