package com.tgedr.labs.microservices.blueprint.store.exceptions;

public class StoreException extends Exception {

	public StoreException() {
		super();
	}

	public StoreException(String msg) {
		super(msg);
	}

	public StoreException(Throwable cause) {
		super(cause);
	}

	public StoreException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public StoreException(String msg, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(msg, cause, enableSuppression, writableStackTrace);
	}

}
