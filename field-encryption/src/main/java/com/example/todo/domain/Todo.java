package com.example.todo.domain;

import com.example.todo.crypto.EncryptedStringConverter;
import com.example.todo.listener.TodoHashListener;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Todo entity.
 *
 * Encrypted fields (stored as "ENC:..." ciphertext in DB):
 *   - title       → also has title_hash for exact-match search
 *   - description → encrypted, not searchable
 *
 * Plain fields (no sensitive data, no encryption needed):
 *   - completed, created_at, updated_at
 *
 * The @EntityListeners annotation tells Hibernate to call TodoHashListener
 * before each insert/update to compute hash columns.
 */
@Entity
@Table(name = "todos")
@EntityListeners(TodoHashListener.class)
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // Stored encrypted. @Convert wires the transparent encrypt/decrypt.
    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "title", nullable = false)
    private String title;

    // HMAC-SHA256 blind index — written by TodoHashListener before each save.
    // Never set this manually; let the listener handle it.
    @Column(name = "title_hash", length = 64)
    private String titleHash;

    // Stored encrypted. No hash column — description is not searchable.
    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "description")
    private String description;

    @Column(name = "completed", nullable = false)
    private boolean completed = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ─── Lifecycle ──────────────────────────────────────────────────────────

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ─── Getters / Setters ──────────────────────────────────────────────────

    public UUID getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTitleHash() { return titleHash; }
    public void setTitleHash(String titleHash) { this.titleHash = titleHash; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
