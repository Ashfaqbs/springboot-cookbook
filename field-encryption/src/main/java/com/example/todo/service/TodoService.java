package com.example.todo.service;

import com.example.todo.crypto.EncryptionService;
import com.example.todo.domain.Todo;
import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.repository.TodoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TodoService {

    private static final Logger log = LoggerFactory.getLogger(TodoService.class);

    private final TodoRepository todoRepository;
    private final EncryptionService encryptionService;

    public TodoService(TodoRepository todoRepository, EncryptionService encryptionService) {
        this.todoRepository    = todoRepository;
        this.encryptionService = encryptionService;
    }

    // ─── Read ────────────────────────────────────────────────────────────────

    public List<TodoResponse> findAll() {
        return todoRepository.findAll().stream()
            .map(TodoResponse::from)
            .toList();
    }

    public TodoResponse findById(UUID id) {
        return todoRepository.findById(id)
            .map(TodoResponse::from)
            .orElseThrow(() -> new TodoNotFoundException(id));
    }

    /**
     * Search by title using the blind index.
     *
     * We hash the search term here and query title_hash column.
     * No decryption happens during the DB lookup — it's just a hash comparison.
     */
    public List<TodoResponse> findByTitle(String title) {
        String titleHash = encryptionService.hash(title);
        log.debug("Searching by title hash: {}...", titleHash.substring(0, 8));

        return todoRepository.findByTitleHash(titleHash).stream()
            .map(TodoResponse::from)
            .toList();
    }

    // ─── Write ───────────────────────────────────────────────────────────────

    @Transactional
    public TodoResponse create(TodoRequest request) {
        Todo todo = new Todo();
        todo.setTitle(request.title());
        todo.setDescription(request.description());
        todo.setCompleted(request.completed());

        // Saving triggers:
        // 1. TodoHashListener.computeHashes() — computes title_hash from plaintext
        // 2. EncryptedStringConverter.convertToDatabaseColumn() — encrypts title + description
        Todo saved = todoRepository.save(todo);
        log.debug("Created todo id={}", saved.getId());
        return TodoResponse.from(saved);
    }

    @Transactional
    public TodoResponse update(UUID id, TodoRequest request) {
        Todo todo = todoRepository.findById(id)
            .orElseThrow(() -> new TodoNotFoundException(id));

        todo.setTitle(request.title());
        todo.setDescription(request.description());
        todo.setCompleted(request.completed());

        // Same flow as create: listener hashes, converter encrypts
        Todo saved = todoRepository.save(todo);
        log.debug("Updated todo id={}", saved.getId());
        return TodoResponse.from(saved);
    }

    @Transactional
    public void delete(UUID id) {
        if (!todoRepository.existsById(id)) {
            throw new TodoNotFoundException(id);
        }
        todoRepository.deleteById(id);
        log.debug("Deleted todo id={}", id);
    }
}
