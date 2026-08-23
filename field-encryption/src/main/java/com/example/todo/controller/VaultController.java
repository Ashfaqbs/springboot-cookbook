package com.example.todo.controller;

import com.example.todo.crypto.EncryptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Demo: encryption using a plain Map as the "store".
 *
 * Shows the same encrypt/decrypt pattern without JPA — useful to understand
 * that EncryptionService is just a utility; the storage layer doesn't matter.
 *
 * The map holds ciphertext. Callers always get back plaintext.
 *
 * POST /api/v1/vault/{key}   body: {"value": "secret"}  → stores ENC:...
 * GET  /api/v1/vault/{key}                              → returns plaintext
 */
@RestController
@RequestMapping("/api/v1/vault")
public class VaultController {

    // key → ENC:... (ciphertext stored, plaintext never kept in memory after encryption)
    private final Map<String, String> store = new ConcurrentHashMap<>();

    private final EncryptionService encryptionService;

    public VaultController(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    @PostMapping("/{key}")
    public Map<String, String> store(@PathVariable String key, @RequestBody Map<String, String> body) {
        String plaintext  = body.get("value");
        String ciphertext = encryptionService.encrypt(plaintext);
        store.put(key, ciphertext);

        return Map.of(
            "key",        key,
            "stored_as",  ciphertext   // showing the raw ciphertext so you can see what the "DB" holds
        );
    }

    @GetMapping("/{key}")
    public ResponseEntity<Map<String, String>> retrieve(@PathVariable String key) {
        String ciphertext = store.get(key);
        if (ciphertext == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(Map.of(
            "key",   key,
            "value", encryptionService.decrypt(ciphertext)  // always returns plaintext
        ));
    }
}
