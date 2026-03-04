package com.imogenlay.ecs.common.error;
 
import org.springframework.http.HttpStatus; 

public class BadRequestException extends ExceptionBase { 
    public BadRequestException(String message) { super(HttpStatus.BAD_REQUEST, message); }  
}
