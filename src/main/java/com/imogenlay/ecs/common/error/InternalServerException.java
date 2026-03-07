package com.imogenlay.ecs.common.error;

import org.springframework.http.HttpStatus;

public class InternalServerException extends ExceptionBase
{
	public InternalServerException(String message) { super(HttpStatus.INTERNAL_SERVER_ERROR, message); }

	@Override
	public String getTitle() { return "Server error"; }
}
