package com.example.projectdemo.domain.attendance.util;

import io.jsonwebtoken.*;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class QRTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.qr.expiration}")
    private Long qrExpiration;

    /**
     * Base64 URL-safe 디코딩된 시크릿 키 생성
     */
    private Key getSigningKey() {
        byte[] keyBytes = Base64.getUrlDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Extract employee number from token
    public String extractEmpNum(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract expiration date from token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract a specific claim from token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Check if token is expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Generate QR token for attendance
    public String generateQRToken(String empNum, String attendanceType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", attendanceType);
        claims.put("timestamp", System.currentTimeMillis());
        return createQRToken(claims, empNum);
    }

    // Create QR token with claims and subject
    private String createQRToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + qrExpiration * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // HS256으로 변경
                .compact();
    }

    // Validate QR token
    public Boolean validateQRToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            System.out.println("만료된 토큰: " + e.getMessage());
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("유효하지 않은 토큰: " + e.getMessage());
            return false;
        }
    }

    // Get attendance type from token
    public String getAttendanceTypeFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("type", String.class);
    }
}