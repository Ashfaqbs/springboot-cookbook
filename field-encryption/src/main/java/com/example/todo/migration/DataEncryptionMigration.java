package com.example.todo.migration;

import com.example.todo.domain.Todo;
import com.example.todo.repository.TodoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Startup migration: encrypts any rows that were inserted before encryption was added.
 *
 * How it works:
 *   1. Native SQL query finds rows where title NOT LIKE 'ENC:%' (raw plaintext in DB).
 *   2. Hibernate loads those rows through the AttributeConverter — converter passes
 *      through the plaintext (no ENC: prefix → no decryption attempted).
 *   3. We save them back — TodoHashListener computes hashes, converter encrypts.
 *
 * Idempotent: already-encrypted rows are never matched by the NOT LIKE query.
 * Safe to restart: if the app crashes mid-migration, re-running picks up where it left off.
 */
@Component
public class DataEncryptionMigration implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataEncryptionMigration.class);

    private final TodoRepository todoRepository;

    public DataEncryptionMigration(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        List<Todo> plaintextTodos = todoRepository.findAllWithPlaintextTitle();

        if (plaintextTodos.isEmpty()) {
            log.info("DataEncryptionMigration: no plaintext rows found, nothing to migrate");
            return;
        }

        log.warn("DataEncryptionMigration: found {} row(s) with plaintext data — encrypting now",
            plaintextTodos.size());

        for (Todo todo : plaintextTodos) {
            // title is plaintext here (converter passed it through on read)
            // saving triggers: listener hashes + converter encrypts
            todoRepository.save(todo);
            log.debug("Migrated todo id={}", todo.getId());
        }

        log.info("DataEncryptionMigration: completed — {} row(s) encrypted", plaintextTodos.size());
    }
}
