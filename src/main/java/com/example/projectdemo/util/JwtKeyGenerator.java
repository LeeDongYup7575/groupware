package com.example.projectdemo.util;

import java.security.Key;
import java.util.Base64;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JwtKeyGenerator {
    public static String generateBase64UrlSafeSecret() {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(key.getEncoded());
    }

    public static void main(String[] args) {
        String secret = generateBase64UrlSafeSecret();
        System.out.println("Generated JWT Secret: " + secret);
        System.out.println("Copy this secret to your application.properties or application.yml");
    }
}