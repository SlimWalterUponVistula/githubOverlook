package com.smartepsilon.backend.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExternalServiceUnhealthyMapper implements ExceptionMapper<ExternalServiceUnhealthy> {
	
	public Response toResponse(ExternalServiceUnhealthy exception) {
		return Response.status(Response.Status.SERVICE_UNAVAILABLE)
				       .entity(exception.getMessage())
				       .build();
	}
}
