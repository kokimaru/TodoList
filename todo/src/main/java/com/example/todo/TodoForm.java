package com.example.todo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class TodoForm {
    private long Id;

    @NotEmpty
    @Size(min = 1, max =30)
    private String title;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;

    private boolean status;
}