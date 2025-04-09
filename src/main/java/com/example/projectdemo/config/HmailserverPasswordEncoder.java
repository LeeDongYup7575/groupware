package com.example.projectdemo.config;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class HmailserverPasswordEncoder {

    // 6자리 소문자 hex salt 생성
    public static String generateSalt(int length) {
        SecureRandom random = new SecureRandom();
        String hex = "0123456789abcdef";
        StringBuilder salt = new StringBuilder();
        for (int i = 0; i < length; i++) {
            salt.append(hex.charAt(random.nextInt(hex.length())));
        }
        return salt.toString();
    }

    // 비밀번호 암호화 (랜덤 솔트)
    public static String encode(String password) {
        String salt = generateSalt(6); // 6자리 salt
        return encodeWithSalt(password, salt);
    }

    // 비밀번호 암호화 (지정된 salt로)
    public static String encodeWithSalt(String password, String salt) {
        try {
            String input = salt + password;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.US_ASCII)); // ASCII 인코딩 중요
            return salt + bytesToHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("hMailServer 암호화 실패", e);
        }
    }

    // 바이트 배열 → 소문자 hex 문자열
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
}
