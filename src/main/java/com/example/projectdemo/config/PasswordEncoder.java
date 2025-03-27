package com.example.projectdemo.config;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;

@Component
public class PasswordEncoder {

    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    public String encode(String rawPassword) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            KeySpec spec = new PBEKeySpec(rawPassword.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hashedPassword = factory.generateSecret(spec).getEncoded();

            byte[] combinedBytes = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combinedBytes, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combinedBytes, salt.length, hashedPassword.length);

            return Base64.getUrlEncoder().withoutPadding().encodeToString(combinedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Password encoding failed", e);
        }
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        try {
            byte[] combinedBytes = Base64.getUrlDecoder().decode(encodedPassword);

            byte[] salt = new byte[SALT_LENGTH];
            byte[] storedHashedPassword = new byte[combinedBytes.length - SALT_LENGTH];
            System.arraycopy(combinedBytes, 0, salt, 0, SALT_LENGTH);
            System.arraycopy(combinedBytes, SALT_LENGTH, storedHashedPassword, 0, storedHashedPassword.length);

            KeySpec spec = new PBEKeySpec(rawPassword.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hashedRawPassword = factory.generateSecret(spec).getEncoded();

            return MessageDigest.isEqual(hashedRawPassword, storedHashedPassword);
        } catch (IllegalArgumentException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Password matching failed", e);
        }
    }
}