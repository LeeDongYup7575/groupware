package com.example.projectdemo.tmp;

import com.example.projectdemo.domain.employees.entity.Employees;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.security.Key;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TmpJwtUtil {
    // 간단한 비밀키 (개발 환경용)
    private static final String SECRET_KEY = "devSecretKeyForTestingPurposesOnly12345678901234567890";
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 24시간

    // 서명 키 생성
    private static Key getSigningKey() {
        byte[] apiKeySecretBytes = Base64.getEncoder().encode(SECRET_KEY.getBytes());
        return new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    // 개발용 토큰 생성 다시
    public static Employees getDevUser() {
        // JWT 토큰에 넣는 것과 동일한 개발용 사용자 정보 생성
        return Employees.builder()
                .id(1)
                .empNum("99999")
                .name("사용자")
                .gender("M")
                .phone("010-1234-5678")
                .email("tmp@techx.com")
                .internalEmail("tmp@techx.com")
                .profileImgUrl("/images/profiles/default.png")
                .depId(1)
                .posId(3)
                .hireDate(LocalDate.of(2020, 1, 1))
                .attendStatus("출근") // HTML 템플릿과 일치하도록 "NORMAL"에서 변경
                .salary(new BigDecimal("5000000"))
                .enabled(true)
                .role("ROLE_ADMIN")
                .lastLogin(LocalDateTime.now())
                .registered(true)
                .totalLeave(15)
                .usedLeave(5)
                .build();
    }

    // 개발용 토큰 생성
    public static String createDevToken() {
        // 개발용 사용자 정보 생성
        Employees devUser = Employees.builder()
                .id(1)
                .empNum("99999")
                .name("사용자")
                .gender("M")
                .phone("010-1234-5678")
                .email("tmp@techx.com")
                .internalEmail("tmp@techx.com")
                .profileImgUrl("/images/profiles/default.png")
                .depId(1)
                .posId(3)
                .hireDate(LocalDate.of(2020, 1, 1))
                .attendStatus("출근")
                .salary(new BigDecimal("5000000"))
                .enabled(true)
                .role("ROLE_ADMIN")
                .lastLogin(LocalDateTime.now())
                .registered(true)
                .totalLeave(15)
                .usedLeave(5)
                .build();

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", devUser.getId());
        claims.put("empNum", devUser.getEmpNum());
        claims.put("name", devUser.getName());
        claims.put("gender", devUser.getGender());
        claims.put("phone", devUser.getPhone());
        claims.put("email", devUser.getEmail());
        claims.put("internalEmail", devUser.getInternalEmail());
        claims.put("depId", devUser.getDepId());
        claims.put("posId", devUser.getPosId());
        claims.put("role", devUser.getRole());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(devUser.getEmpNum())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
