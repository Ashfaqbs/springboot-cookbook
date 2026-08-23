package com.example.todo.dto;

import com.example.todo.domain.Todo;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * API response — always exposes plaintext values.
 * The converter decrypts on read, so Todo fields are already plaintext here.
 */
public record TodoResponse(
    UUID id,
    String title,
    String description,
    boolean completed,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static TodoResponse from(Todo todo) {
        return new TodoResponse(
            todo.getId(),
            todo.getTitle(),          // plaintext — converter already decrypted
            todo.getDescription(),    // plaintext — converter already decrypted
            todo.isCompleted(),
            todo.getCreatedAt(),
            todo.getUpdatedAt()
        );
    }
}
