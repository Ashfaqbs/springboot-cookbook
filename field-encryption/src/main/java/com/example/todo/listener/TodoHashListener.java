package com.example.todo.listener;

import com.example.todo.crypto.EncryptionService;
import com.example.todo.domain.Todo;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

/**
 * JPA EntityListener that auto-computes blind index hash columns before every save.
 *
 * Why static field instead of @Autowired?
 *   JPA EntityListeners are instantiated by Hibernate (new TodoHashListener()),
 *   not by Spring — so Spring cannot inject dependencies via constructor or field.
 *   We solve this with a static field populated by EntityListenerConfig at startup.
 *
 * Ordering guarantee:
 *   @PrePersist / @PreUpdate fires BEFORE AttributeConverter runs.
 *   So at this point, todo.getTitle() is still plaintext — we can hash it correctly.
 *   Then the AttributeConverter encrypts the field value when Hibernate builds the SQL.
 */
public class TodoHashListener {

    private static EncryptionService encryptionService;

    /**
     * Called once by EntityListenerConfig after Spring context is ready.
     */
    public static void setEncryptionService(EncryptionService service) {
        TodoHashListener.encryptionService = service;
    }

    @PrePersist
    @PreUpdate
    public void computeHashes(Todo todo) {
        if (encryptionService == null) return;

        // title_hash enables: WHERE title_hash = HMAC('buy milk')
        todo.setTitleHash(encryptionService.hash(todo.getTitle()));

        // description has no hash — we don't support searching by description
    }
}
