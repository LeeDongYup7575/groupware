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

    private static final int ITERATION_COUNT = 65536; // NIST recommended iteration count
    private static final int KEY_LENGTH = 256; // Recommended key length
    private static final int SALT_LENGTH = 16; // Recommended salt length

    public String encode(String rawPassword) {
        try {
            // Generate a cryptographically strong random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            // Use PBKDF2 with HMAC SHA-256 for key derivation
            KeySpec spec = new PBEKeySpec(rawPassword.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hashedPassword = factory.generateSecret(spec).getEncoded();

            // Combine salt and hashed password for storage
            byte[] combinedBytes = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combinedBytes, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combinedBytes, salt.length, hashedPassword.length);

            // Use URL-safe Base64 encoding to avoid problematic characters
            return Base64.getUrlEncoder().withoutPadding().encodeToString(combinedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Password encoding failed", e);
        }
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        try {
            // Decode from URL-safe Base64
            byte[] combinedBytes = Base64.getUrlDecoder().decode(encodedPassword);

            // Extract the salt (first 16 bytes)
            byte[] salt = new byte[SALT_LENGTH];
            byte[] storedHashedPassword = new byte[combinedBytes.length - SALT_LENGTH];
            System.arraycopy(combinedBytes, 0, salt, 0, SALT_LENGTH);
            System.arraycopy(combinedBytes, SALT_LENGTH, storedHashedPassword, 0, storedHashedPassword.length);

            // Recreate the hash using the same parameters
            KeySpec spec = new PBEKeySpec(rawPassword.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hashedRawPassword = factory.generateSecret(spec).getEncoded();

            // Constant-time comparison to prevent timing attacks
            return MessageDigest.isEqual(hashedRawPassword, storedHashedPassword);
        } catch (IllegalArgumentException e) {
            // Handle potential Base64 decoding errors
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Password matching failed", e);
        }
    }
}