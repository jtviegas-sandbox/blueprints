package com.tgedr.labs.microservices.blueprint.common.exceptions;

public class StateManagerException extends Exception {

	private static final long serialVersionUID = 1L;

	public StateManagerException() {
		super();
	}

	public StateManagerException(String arg0) {
		super(arg0);
	}

	public StateManagerException(Throwable arg0) {
		super(arg0);
	}

	public StateManagerException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
