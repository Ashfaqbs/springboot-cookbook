package com.example.todo;

import com.example.todo.domain.Todo;
import com.example.todo.repository.TodoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Proves that data is actually encrypted in the database.
 *
 * The key scenario: app code sees plaintext, DB column stores ciphertext.
 */
@SpringBootTest
class EncryptionAtRestIntegrationTest {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;  // raw JDBC — bypasses Hibernate converters

    @Test
    void appSeesPlaintextButDBStoresCiphertext() {
        // Save via JPA (triggers converter + listener)
        Todo todo = new Todo();
        todo.setTitle("buy milk");
        todo.setDescription("2L full fat");
        todo.setCompleted(false);
        Todo saved = todoRepository.save(todo);

        // Read back via JPA — converter decrypts automatically
        String titleViaJpa = todoRepository.findById(saved.getId())
            .map(Todo::getTitle)
            .orElseThrow();

        // Read the raw column via JDBC — no converter, raw ciphertext
        String rawTitleInDb = jdbcTemplate.queryForObject(
            "SELECT title FROM todos WHERE id = ?",
            String.class,
            saved.getId().toString()
        );

        String rawTitleHashInDb = jdbcTemplate.queryForObject(
            "SELECT title_hash FROM todos WHERE id = ?",
            String.class,
            saved.getId().toString()
        );

        System.out.println("=== What the app sees ===");
        System.out.println("title via JPA:       " + titleViaJpa);

        System.out.println("\n=== What the database stores ===");
        System.out.println("title in DB (raw):   " + rawTitleInDb);
        System.out.println("title_hash in DB:    " + rawTitleHashInDb);

        // App sees plaintext
        assertThat(titleViaJpa).isEqualTo("buy milk");

        // DB stores ciphertext (starts with "ENC:")
        assertThat(rawTitleInDb)
            .startsWith("ENC:")
            .doesNotContain("buy milk");

        // Hash column is a 64-char hex string (not the plaintext, not the ciphertext)
        assertThat(rawTitleHashInDb)
            .hasSize(64)
            .matches("[0-9a-f]+");
    }

    @Test
    void searchUsesBLindIndexNeverDecrypts() {
        Todo todo = new Todo();
        todo.setTitle("walk the dog");
        todoRepository.save(todo);

        // This query hits title_hash, never reads title column
        // If you ran EXPLAIN on this, you'd see idx_todos_title_hash used
        var results = todoRepository.findByTitleHash(
            // Manually replicate what TodoService.findByTitle() does:
            new com.example.todo.crypto.EncryptionService(
                buildConfig()
            ).hash("walk the dog")
        );

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("walk the dog");  // decrypted by converter
    }

    private com.example.todo.config.EncryptionConfig buildConfig() {
        var config = new com.example.todo.config.EncryptionConfig();
        config.setKey("0123456789abcdef0123456789abcdef");
        config.setHmacKey("fedcba9876543210fedcba9876543210");
        return config;
    }
}
