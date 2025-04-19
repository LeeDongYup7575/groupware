package com.example.projectdemo.domain.auth.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LogoutService {

    // 로그아웃된 토큰을 저장하는 블랙리스트 (메모리 기반)
    private static final Set<String> tokenBlacklist = ConcurrentHashMap.newKeySet();

    public void blacklistToken(String token) {
        if (token != null && !token.isEmpty()) {
            tokenBlacklist.add(token);
        }
    }

    public boolean isTokenBlacklisted(String token) {
        return token != null && tokenBlacklist.contains(token);
    }

}