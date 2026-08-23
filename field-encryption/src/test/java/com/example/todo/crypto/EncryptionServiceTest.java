package com.example.todo.crypto;

import com.example.todo.config.EncryptionConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EncryptionServiceTest {

    private EncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        EncryptionConfig config = new EncryptionConfig();
        config.setKey("0123456789abcdef0123456789abcdef");       // 32 bytes
        config.setHmacKey("fedcba9876543210fedcba9876543210");   // 32 bytes
        encryptionService = new EncryptionService(config);
    }

    // ─── Encrypt / Decrypt ───────────────────────────────────────────────────

    @Test
    void shouldEncryptAndDecryptRoundTrip() {
        String original = "buy milk";

        String encrypted = encryptionService.encrypt(original);
        String decrypted = encryptionService.decrypt(encrypted);

        assertThat(decrypted).isEqualTo(original);
    }

    @Test
    void encryptedValueShouldStartWithENCPrefix() {
        String encrypted = encryptionService.encrypt("hello");

        assertThat(encrypted).startsWith("ENC:");
    }

    @Test
    void samePlaintextEncryptedTwiceShouldProduceDifferentCiphertext() {
        // AES-GCM with random IV: same input → different output every time
        String first  = encryptionService.encrypt("same value");
        String second = encryptionService.encrypt("same value");

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void shouldReturnNullForNullInput() {
        assertThat(encryptionService.encrypt(null)).isNull();
        assertThat(encryptionService.decrypt(null)).isNull();
    }

    @Test
    void shouldPassThroughPlaintextOnDecrypt() {
        // Plaintext without ENC: prefix → passthrough (migration window support)
        String result = encryptionService.decrypt("not encrypted yet");

        assertThat(result).isEqualTo("not encrypted yet");
    }

    @Test
    void shouldDetectIsEncrypted() {
        String encrypted = encryptionService.encrypt("test");

        assertThat(encryptionService.isEncrypted(encrypted)).isTrue();
        assertThat(encryptionService.isEncrypted("plaintext")).isFalse();
        assertThat(encryptionService.isEncrypted(null)).isFalse();
    }

    // ─── Hash / Blind index ──────────────────────────────────────────────────

    @Test
    void hashShouldBeDeterministic() {
        String first  = encryptionService.hash("buy milk");
        String second = encryptionService.hash("buy milk");

        // HMAC is deterministic — same input always produces same hash
        assertThat(first).isEqualTo(second);
    }

    @Test
    void differentValuesShouldProduceDifferentHashes() {
        String h1 = encryptionService.hash("buy milk");
        String h2 = encryptionService.hash("buy bread");

        assertThat(h1).isNotEqualTo(h2);
    }

    @Test
    void hashShouldBe64HexChars() {
        String hash = encryptionService.hash("any value");

        assertThat(hash).hasSize(64).matches("[0-9a-f]+");
    }

    @Test
    void hashShouldReturnNullForNull() {
        assertThat(encryptionService.hash(null)).isNull();
    }

    // ─── Key validation ──────────────────────────────────────────────────────

    @Test
    void shouldFailFastWithShortKey() {
        EncryptionConfig badConfig = new EncryptionConfig();
        badConfig.setKey("tooshort");
        badConfig.setHmacKey("fedcba9876543210fedcba9876543210");

        assertThatThrownBy(badConfig::validate)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("encryption.key must be exactly 32 bytes");
    }
}
