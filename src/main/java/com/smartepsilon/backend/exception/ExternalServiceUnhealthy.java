package com.smartepsilon.backend.exception;

import static com.smartepsilon.backend.exception.ErrorMessages.SERVICE_UNHEALTHY_MESSAGE_TEMPLATE;;

public class ExternalServiceUnhealthy extends RuntimeException {

	private static final long serialVersionUID = 7440618369228125598L;

	public ExternalServiceUnhealthy(int attempts, Exception reason) {
		super(String.format(SERVICE_UNHEALTHY_MESSAGE_TEMPLATE, attempts, reason));
	}
}
