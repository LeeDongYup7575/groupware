package com.example.projectdemo.domain.auth.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LogoutService {

    // 로그아웃된 토큰을 저장하는 블랙리스트 (메모리 기반)
    private static final Set<String> tokenBlacklist = ConcurrentHashMap.newKeySet();

    /**
     * 토큰을 블랙리스트에 추가
     */
    public void blacklistToken(String token) {
        if (token != null && !token.isEmpty()) {
            tokenBlacklist.add(token);
            System.out.println("토큰이 블랙리스트에 추가되었습니다.");
        }
    }

    /**
     * 토큰이 블랙리스트에 있는지 확인
     */
    public boolean isTokenBlacklisted(String token) {
        return token != null && tokenBlacklist.contains(token);
    }

    /**
     * 블랙리스트 토큰 개수 조회 (디버깅용)
     */
    public int getBlacklistSize() {
        return tokenBlacklist.size();
    }
}