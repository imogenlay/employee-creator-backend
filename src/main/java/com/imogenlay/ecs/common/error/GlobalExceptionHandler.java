package com.imogenlay.ecs.common.error;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.imogenlay.ecs.common.error.dto.ExceptionResponse;

import jakarta.servlet.http.HttpServletRequest;  

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
                
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {

            String fieldName;
            if (error instanceof FieldError fieldError) 
                fieldName = fieldError.getField();
            else 
                fieldName = error.getObjectName();
            
            errors.put(fieldName, error.getDefaultMessage());
        });
        

        BadRequestException exception = new BadRequestException(ex.getMessage());
        return build(exception, request);
    }
     
    @ExceptionHandler(ExceptionBase.class)
    private ResponseEntity<ExceptionResponse> build(ExceptionBase ex, HttpServletRequest request) {
        return build(ex.getMessage(), ex.getStatus(), request, new HashMap<>());
    }

    private ResponseEntity<ExceptionResponse> build(String message, HttpStatus status, HttpServletRequest request, Map<String, String> details) {
        String path = request.getRequestURI(); 

        ExceptionResponse response =
            new ExceptionResponse(
                message,
                path,
                status.value(),
                LocalDateTime.now(),
                details);
        return new ResponseEntity<>(response, status);
    }
}
