package com.example.todo.crypto;

import com.example.todo.config.EncryptionConfig;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Core encryption service.
 *
 * Encryption: AES-256-GCM
 *   - Authenticated encryption: decryption fails if ciphertext was tampered with.
 *   - Random 12-byte IV per encryption → same plaintext encrypted twice produces different ciphertext.
 *   - Output format: "ENC:" + Base64(IV[12] + ciphertext + auth_tag[16])
 *
 * Hashing: HMAC-SHA256 (blind index for searchable encrypted fields)
 *   - Deterministic: same input always produces same output.
 *   - One-way: cannot recover plaintext from hash.
 *   - Output format: 64-char lowercase hex string.
 */
@Service
public class EncryptionService {

    static final String ENC_PREFIX = "ENC:";

    private static final String AES_ALGORITHM = "AES/GCM/NoPadding";
    private static final int    IV_LENGTH_BYTES = 12;   // 96-bit IV, recommended for GCM
    private static final int    GCM_TAG_BITS    = 128;  // 16-byte auth tag

    private final SecretKeySpec aesKey;
    private final SecretKeySpec hmacKey;
    private final String        hmacAlgorithm;

    public EncryptionService(EncryptionConfig config) {
        this.aesKey        = new SecretKeySpec(config.getKey().getBytes(StandardCharsets.UTF_8), "AES");
        this.hmacAlgorithm = config.getHmacAlgorithm();
        this.hmacKey       = new SecretKeySpec(config.getHmacKey().getBytes(StandardCharsets.UTF_8), hmacAlgorithm);
    }

    // ─── Encryption ─────────────────────────────────────────────────────────

    /**
     * Encrypts plaintext with AES-256-GCM.
     *
     * Write path:
     *   "buy milk" → "ENC:AgF+xK3mNp..." (random IV every time)
     */
    public String encrypt(String plaintext) {
        if (plaintext == null) return null;

        try {
            byte[] iv = generateIv();

            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, new GCMParameterSpec(GCM_TAG_BITS, iv));

            // ciphertext already includes the 16-byte GCM auth tag appended by Java
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // Pack: [12-byte IV][ciphertext+tag] → Base64 → prefix "ENC:"
            byte[] combined = new byte[IV_LENGTH_BYTES + ciphertext.length];
            System.arraycopy(iv,         0, combined, 0,               IV_LENGTH_BYTES);
            System.arraycopy(ciphertext, 0, combined, IV_LENGTH_BYTES, ciphertext.length);

            return ENC_PREFIX + Base64.getEncoder().encodeToString(combined);

        } catch (Exception e) {
            throw new IllegalStateException("Encryption failed", e);
        }
    }

    /**
     * Decrypts ciphertext produced by {@link #encrypt}.
     *
     * Read path:
     *   "ENC:AgF+xK3mNp..." → "buy milk"
     *
     * Plaintext passthrough: if the value does not start with "ENC:", it is
     * returned as-is. This supports the migration window where the DB still
     * has unencrypted rows.
     */
    public String decrypt(String ciphertext) {
        if (ciphertext == null)                    return null;
        if (!ciphertext.startsWith(ENC_PREFIX))    return ciphertext;  // plaintext passthrough

        try {
            byte[] combined  = Base64.getDecoder().decode(ciphertext.substring(ENC_PREFIX.length()));

            byte[] iv         = Arrays.copyOfRange(combined, 0,               IV_LENGTH_BYTES);
            byte[] encrypted  = Arrays.copyOfRange(combined, IV_LENGTH_BYTES, combined.length);

            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, aesKey, new GCMParameterSpec(GCM_TAG_BITS, iv));

            // Throws AEADBadTagException if ciphertext was tampered — do NOT catch this silently
            byte[] plaintext = cipher.doFinal(encrypted);
            return new String(plaintext, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new IllegalStateException("Decryption failed — key mismatch or tampered data", e);
        }
    }

    // ─── Blind index ────────────────────────────────────────────────────────

    /**
     * Computes an HMAC-SHA256 blind index for searchable fields.
     *
     * Same input → same 64-char hex output (deterministic).
     * Used in WHERE title_hash = hash('buy milk') instead of WHERE title = 'buy milk'.
     */
    public String hash(String value) {
        if (value == null) return null;

        try {
            Mac mac = Mac.getInstance(hmacAlgorithm);
            mac.init(hmacKey);
            byte[] hmacBytes = mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hmacBytes);  // 64-char lowercase hex
        } catch (Exception e) {
            throw new IllegalStateException("HMAC hashing failed", e);
        }
    }

    // ─── Helpers ────────────────────────────────────────────────────────────

    public boolean isEncrypted(String value) {
        return value != null && value.startsWith(ENC_PREFIX);
    }

    private byte[] generateIv() {
        byte[] iv = new byte[IV_LENGTH_BYTES];
        new SecureRandom().nextBytes(iv);
        return iv;
    }
}
