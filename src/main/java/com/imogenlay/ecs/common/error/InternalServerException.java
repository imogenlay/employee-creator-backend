package com.imogenlay.ecs.common.error;

import org.springframework.http.HttpStatus;

public class InternalServerException extends ExceptionBase
{
	public final static String TITLE = "Internal server error";

	public InternalServerException(String message) { super(HttpStatus.INTERNAL_SERVER_ERROR, message); }

	@Override
	public String getTitle() { return TITLE; }
}
