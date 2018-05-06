package com.smartepsilon.backend.exception;

import static com.smartepsilon.backend.exception.ErrorMessages.NOT_FOUND_MESSAGE_TEMPLATE;


public class RepositoryNotFound extends RuntimeException {
	
	private static final long serialVersionUID = 7440618369228125598L;

	public RepositoryNotFound(String requestedOwner, String requestedId) {
		super(String.format(NOT_FOUND_MESSAGE_TEMPLATE, requestedOwner, requestedId));
	}
}
