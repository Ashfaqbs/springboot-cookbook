package com.example.todo.config;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.StandardCharsets;

/**
 * Binds encryption.key and encryption.hmac-key from application.yml / env vars.
 *
 * Application fails fast on startup if keys are missing or wrong length.
 * In production supply via environment variables:
 *   ENCRYPTION_KEY=<32-byte value>
 *   ENCRYPTION_HMAC_KEY=<32-byte value>
 */
@ConfigurationProperties(prefix = "encryption")
public class EncryptionConfig {

    private String key;
    private String hmacKey;

    /**
     * HMAC algorithm used for blind indexes.
     * Valid values: HmacSHA256 (default), HmacSHA384, HmacSHA512
     * Changing this after data exists will break all existing searches — hashes won't match.
     */
    private String hmacAlgorithm = "HmacSHA256";

    private static final java.util.Set<String> SUPPORTED_HMAC_ALGORITHMS =
        java.util.Set.of("HmacSHA256", "HmacSHA384", "HmacSHA512");

    @PostConstruct
    public void validate() {
        if (key == null || key.getBytes(StandardCharsets.UTF_8).length != 32) {
            throw new IllegalStateException(
                "encryption.key must be exactly 32 bytes for AES-256. Got: "
                    + (key == null ? "null" : key.getBytes(StandardCharsets.UTF_8).length + " bytes"));
        }
        if (hmacKey == null || hmacKey.getBytes(StandardCharsets.UTF_8).length != 32) {
            throw new IllegalStateException(
                "encryption.hmac-key must be exactly 32 bytes. Got: "
                    + (hmacKey == null ? "null" : hmacKey.getBytes(StandardCharsets.UTF_8).length + " bytes"));
        }
        if (!SUPPORTED_HMAC_ALGORITHMS.contains(hmacAlgorithm)) {
            throw new IllegalStateException(
                "encryption.hmac-algorithm must be one of " + SUPPORTED_HMAC_ALGORITHMS + ". Got: " + hmacAlgorithm);
        }
    }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getHmacKey() { return hmacKey; }
    public void setHmacKey(String hmacKey) { this.hmacKey = hmacKey; }

    public String getHmacAlgorithm() { return hmacAlgorithm; }
    public void setHmacAlgorithm(String hmacAlgorithm) { this.hmacAlgorithm = hmacAlgorithm; }
}
