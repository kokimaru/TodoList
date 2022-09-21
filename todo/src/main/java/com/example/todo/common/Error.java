package com.example.todo.common;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class Error {
    public static void setError(BindingResult error, String field, String errorMsg){
        FieldError fieldError = new FieldError(error.getObjectName(), "filePath", errorMsg);
        error.addError(fieldError);
    }
}
