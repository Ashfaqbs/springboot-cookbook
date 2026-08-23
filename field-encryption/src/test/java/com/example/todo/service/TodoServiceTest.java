package com.example.todo.service;

import com.example.todo.config.EncryptionConfig;
import com.example.todo.crypto.EncryptionService;
import com.example.todo.domain.Todo;
import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    private TodoService todoService;
    private EncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        EncryptionConfig config = new EncryptionConfig();
        config.setKey("0123456789abcdef0123456789abcdef");
        config.setHmacKey("fedcba9876543210fedcba9876543210");
        encryptionService = new EncryptionService(config);
        todoService = new TodoService(todoRepository, encryptionService);
    }

    @Test
    void shouldReturnAllTodos() {
        Todo todo = makeTodo("buy milk", null, false);
        when(todoRepository.findAll()).thenReturn(List.of(todo));

        List<TodoResponse> result = todoService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo("buy milk");
    }

    @Test
    void shouldThrowWhenTodoNotFound() {
        UUID id = UUID.randomUUID();
        when(todoRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.findById(id))
            .isInstanceOf(TodoNotFoundException.class);
    }

    @Test
    void shouldSearchByTitleUsingBlindIndex() {
        String title = "buy milk";
        String titleHash = encryptionService.hash(title);  // same hash the service computes
        Todo todo = makeTodo(title, null, false);

        when(todoRepository.findByTitleHash(titleHash)).thenReturn(List.of(todo));

        List<TodoResponse> result = todoService.findByTitle(title);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).title()).isEqualTo(title);
        // Verify it queries by hash, not plaintext
        verify(todoRepository).findByTitleHash(titleHash);
        verify(todoRepository, never()).findAll();
    }

    @Test
    void shouldCreateTodo() {
        TodoRequest request = new TodoRequest("buy milk", "from the store", false);
        Todo saved = makeTodo("buy milk", "from the store", false);
        when(todoRepository.save(any(Todo.class))).thenReturn(saved);

        TodoResponse result = todoService.create(request);

        assertThat(result.title()).isEqualTo("buy milk");
        assertThat(result.description()).isEqualTo("from the store");
        verify(todoRepository).save(any(Todo.class));
    }

    @Test
    void shouldDeleteTodo() {
        UUID id = UUID.randomUUID();
        when(todoRepository.existsById(id)).thenReturn(true);

        todoService.delete(id);

        verify(todoRepository).deleteById(id);
    }

    @Test
    void shouldThrowWhenDeletingNonExistentTodo() {
        UUID id = UUID.randomUUID();
        when(todoRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> todoService.delete(id))
            .isInstanceOf(TodoNotFoundException.class);
    }

    private Todo makeTodo(String title, String description, boolean completed) {
        Todo todo = new Todo();
        todo.setTitle(title);
        todo.setDescription(description);
        todo.setCompleted(completed);
        // Simulate @PrePersist
        try {
            var m = Todo.class.getDeclaredMethod("onCreate");
            m.setAccessible(true);
            m.invoke(todo);
        } catch (Exception ignored) {}
        return todo;
    }
}
