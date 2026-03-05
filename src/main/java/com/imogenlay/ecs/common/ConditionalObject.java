package com.imogenlay.ecs.common;

import com.imogenlay.ecs.common.error.BadRequestException;
import com.imogenlay.ecs.common.error.InternalServerException;
import com.imogenlay.ecs.common.error.NotFoundException;
import org.springframework.http.HttpStatus;

public class ConditionalObject<T>
{

	private final T object;
	private final Tuple<HttpStatus, String> error;

	public ConditionalObject(T object)
	{
		this.object = object;
		this.error = null;
	}

	public ConditionalObject(HttpStatus status, String message)
	{
		this.object = null;
		this.error = new Tuple<HttpStatus, String>(status, message);
	}
    
	public T getObject() { return object; }

	public HttpStatus getErrorStatus() { return hasError() ? error.getA() : null; }

	public String getErrorMessage() { return hasError() ? error.getB() : null; }

	public boolean hasError() { return error != null; }

	public boolean hasObject() { return !hasError() && object != null; }

	public <R> ConditionalObject<R> copyError()
	{
		return new ConditionalObject<R>(getErrorStatus(), getErrorMessage());
	}

	public void throwError()
	{
		if (!hasError())
			return;

		HttpStatus status = getErrorStatus();
		switch (status)
		{
			case BAD_REQUEST:
				throw new BadRequestException(getErrorMessage());
			case NOT_FOUND:
				throw new NotFoundException(getErrorMessage());
			case INTERNAL_SERVER_ERROR:
				throw new InternalServerException(getErrorMessage());
			default:
				throw new RuntimeException("Error was not recognised: " + getErrorMessage());
		}
	}
}
