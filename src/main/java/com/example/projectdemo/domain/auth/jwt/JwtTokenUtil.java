package com.example.projectdemo.domain.auth.jwt;

import com.example.projectdemo.domain.employees.dto.EmployeesDTO;
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
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration:604800000}") // 기본값 7일
    private long refreshExpiration;

    @Value("${jwt.qr.expiration:300}") // 기본값 5분 (초 단위)
    private long qrExpiration;

    /**
     * Base64 URL-safe 디코딩된 시크릿 키 생성
     */
    private Key getSigningKey() {
        byte[] keyBytes = Base64.getUrlDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 토큰에서 사원번호 추출
     */
    public String getEmpNumFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 토큰에서 만료 시간 추출
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 토큰에서 역할 추출
     */
    public String getRoleFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("role", String.class);
    }

    /**
     * 토큰에서 임시 비밀번호 상태 추출
     */
    public boolean getTempPasswordStatusFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("tempPassword", Boolean.class);
    }

    public int getIdFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("id", Integer.class);
    }

    /**
     * 토큰에서 클레임 추출
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 비밀키를 사용하여 토큰에서 모든 클레임 추출
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 토큰 만료 확인
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 직원 정보로 액세스 토큰 생성
     */
    public String generateToken(EmployeesDTO employee) {
        System.out.println("geneate token: "+employee);
        Map<String, Object> claims = new HashMap<>();
        // 기본 인증 정보
        claims.put("id", employee.getId());
        claims.put("role", employee.getRole());
        claims.put("name", employee.getName());
        claims.put("empNum", employee.getEmpNum());
        claims.put("email", employee.getEmail());

        // 부가 정보
        claims.put("gender", employee.getGender());
        claims.put("phone", employee.getPhone());
        claims.put("profileImgUrl", employee.getProfileImgUrl());

        // 근무 관련 정보
        claims.put("depId", employee.getDepId());
        claims.put("posId", employee.getPosId());
        claims.put("hireDate", employee.getHireDate().toString());
        claims.put("attendStatus", employee.getAttendStatus());

        // 휴가 관련 정보
        claims.put("totalLeave", employee.getTotalLeave());
        claims.put("usedLeave", employee.getUsedLeave());

        return doGenerateToken(claims, employee.getEmpNum(), jwtExpiration);
    }

    /**
     * 임시 비밀번호 상태와 함께 토큰 생성
     */
    public String generateToken(EmployeesDTO employee, boolean tempPassword) {
        Map<String, Object> claims = new HashMap<>();

        // 기본 인증 정보
        claims.put("id", employee.getId());
        claims.put("role", employee.getRole());
        claims.put("name", employee.getName());
        claims.put("empNum", employee.getEmpNum());
        claims.put("email", employee.getEmail());
        claims.put("tempPassword", tempPassword);

        // 부가 정보
        claims.put("gender", employee.getGender());
        claims.put("phone", employee.getPhone());
        claims.put("profileImgUrl", employee.getProfileImgUrl());

        // 근무 관련 정보
        claims.put("depId", employee.getDepId());
        claims.put("posId", employee.getPosId());
        claims.put("hireDate", employee.getHireDate().toString());
        claims.put("attendStatus", employee.getAttendStatus());

        // 휴가 관련 정보
        claims.put("totalLeave", employee.getTotalLeave());
        claims.put("usedLeave", employee.getUsedLeave());

        return doGenerateToken(claims, employee.getEmpNum(), jwtExpiration);
    }

    /**
     * 리프레시 토큰 생성
     */
    public String generateRefreshToken(EmployeesDTO employee) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, employee.getEmpNum(), refreshExpiration);
    }

    /**
     * QR 토큰 생성
     */
    public String generateQRToken(String empNum, String attendanceType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", attendanceType);
        claims.put("timestamp", System.currentTimeMillis());
        return doGenerateToken(claims, empNum, qrExpiration * 1000);
    }

    /**
     * QR 토큰에서 출결 유형 추출
     */
    public String getAttendanceTypeFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("type", String.class);
    }

    /**
     * QR 토큰에서 타임스탬프 추출
     */
    public Long getTimestampFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("timestamp", Long.class);
    }

    private String doGenerateToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰 검증
     */
    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            System.out.println("토큰 만료: " + e.getMessage());
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("유효하지 않은 토큰: " + e.getMessage());
            return false;
        }
    }
}