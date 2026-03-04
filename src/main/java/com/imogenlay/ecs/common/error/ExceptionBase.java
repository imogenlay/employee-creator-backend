package com.imogenlay.ecs.common.error;

import org.springframework.http.HttpStatus;

public class ExceptionBase extends RuntimeException {
    private final HttpStatus status;
    public ExceptionBase(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
    
    public HttpStatus getStatus() { return status; }
}
