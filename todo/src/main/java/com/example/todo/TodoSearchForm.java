package com.example.todo;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.Size;

@Data
public class TodoSearchForm {
    private Long id;
    @NotNull
    @Size(min = 0, max = 30)
    private String searchWord;
}
