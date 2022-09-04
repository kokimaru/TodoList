package com.example.todo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class TodoExcelForm {
    @NotEmpty
    @Pattern(regexp = "(.*\\.xlsx|.*\\.xls)$", message = "ファイル形式を「xlsx」または「xls」にしてください")
    private String filePath;
}