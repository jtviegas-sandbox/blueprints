package com.tgedr.labs.microservices.blueprint.common.exceptions;

import org.springframework.http.HttpStatus;

public class ApiException extends Exception {

	private static final long serialVersionUID = 1L;

	private HttpStatus statusCode;

	public ApiException() {
		super();
	}

	public ApiException(String message, HttpStatus statusCode) {
		super(message);
		this.statusCode = statusCode;
	}

	public ApiException(Throwable cause, HttpStatus statusCode) {
		super(cause);
		this.statusCode = statusCode;
	}

	public ApiException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApiException(String message, Throwable cause, HttpStatus statusCode) {
		super(message, cause);
		this.statusCode = statusCode;
	}

	public ApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
			HttpStatus statusCode) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.statusCode = statusCode;
	}

	public HttpStatus getStatusCode() {
		return statusCode;
	}

}
