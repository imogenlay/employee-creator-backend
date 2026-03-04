package com.imogenlay.ecs.common.error.dto;

import java.time.LocalDateTime;
import java.util.Map; 

public record ExceptionResponse(
    String message,
    String path,
    int status,
    LocalDateTime timestamp,
    Map<String, String> details
) { }