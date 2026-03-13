package com.imogenlay.ecs.common.error;

import com.imogenlay.ecs.common.error.dto.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler
{
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ExceptionResponse> handleValidationException(
			MethodArgumentNotValidException ex,
			HttpServletRequest request)
	{
		Map<String, String> errors = new HashMap<>();

		ex.getBindingResult().getAllErrors().forEach(error -> {

			String fieldName;
			if (error instanceof FieldError fieldError)
				fieldName = fieldError.getField();
			else
				fieldName = error.getObjectName();

			errors.put(fieldName, error.getDefaultMessage());
		});

		BadRequestException exception = new BadRequestException("Validation failed");
		return build(exception.getMessage(), exception.getStatus(), request, errors);
	}

	@ExceptionHandler(ExceptionBase.class)
	private ResponseEntity<ExceptionResponse> build(ExceptionBase ex, HttpServletRequest request)
	{
		Map<String, String> errors = new HashMap<>();
		errors.put("error", ex.getMessage());
		return build(ex.getTitle(), ex.getStatus(), request, errors);
	}

	@ExceptionHandler(InternalServerException.class)
	private ResponseEntity<ExceptionResponse> build(InternalServerException ex, HttpServletRequest request)
	{
		return buildUnknownInternalError(ex, request);
	}

	@ExceptionHandler(PropertyReferenceException.class)
	private ResponseEntity<ExceptionResponse> build(PropertyReferenceException ex, HttpServletRequest request)
	{
		return buildUnknownInternalError(ex, request);
	}

	private ResponseEntity<ExceptionResponse> buildUnknownInternalError(Exception ex, HttpServletRequest request)
	{
		Map<String, String> errors = new HashMap<>();
		errors.put("error", ex.getMessage());
		errors.put("trace", getTrace(ex));
		return build(InternalServerException.TITLE, HttpStatus.INTERNAL_SERVER_ERROR, request, errors);
	}

	private ResponseEntity<ExceptionResponse> build(String message, HttpStatus status, HttpServletRequest request, Map<String, String> details)
	{
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

	private String getTrace(Exception ex)
	{
		StackTraceElement[] stackTrace = ex.getStackTrace();
		return Arrays.stream(stackTrace)
				.limit(10)
				.map(StackTraceElement::toString)
				.collect(Collectors.joining("\n"));
	}
}
