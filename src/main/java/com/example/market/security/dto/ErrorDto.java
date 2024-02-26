package com.example.market.security.dto;

import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

public class ErrorDto {

    private final int status;

    private final String message;

    private String details;

    private final List<FieldError> fieldErrors = new ArrayList<>();

    public ErrorDto(int status, String message) {
        this.status= status;
        this.message=message;
    }

    public ErrorDto(int status, String message, String details) {
        this.status = status;
        this.message = message;
        this.details = details;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public void addFieldError(String objectName, String path, String message) {
        FieldError error = new FieldError(objectName, path, message);
        fieldErrors.add(error);
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

}
