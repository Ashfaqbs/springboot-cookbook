package com.example.todo.repository;

import com.example.todo.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TodoRepository extends JpaRepository<Todo, UUID> {

    /**
     * Search by title using the blind index.
     *
     * We cannot do WHERE title = 'buy milk' because the DB column stores ciphertext.
     * Instead: hash the search term → look up the deterministic HMAC hash.
     *
     * Caller (TodoService) is responsible for hashing the value before passing it here.
     */
    List<Todo> findByTitleHash(String titleHash);

    /**
     * Used by DataEncryptionMigration to find rows that were inserted before
     * encryption was added (no "ENC:" prefix on the title column).
     */
    @Query(value = "SELECT * FROM todos WHERE title NOT LIKE 'ENC:%'", nativeQuery = true)
    List<Todo> findAllWithPlaintextTitle();
}
