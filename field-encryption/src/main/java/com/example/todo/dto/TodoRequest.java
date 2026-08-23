package com.example.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request body for create and update operations.
 * Records are immutable — validation annotations work normally.
 */
public record TodoRequest(

    @NotBlank(message = "title must not be blank")
    @Size(max = 500, message = "title must be 500 characters or fewer")
    String title,

    @Size(max = 2000, message = "description must be 2000 characters or fewer")
    String description,

    boolean completed
) {}
