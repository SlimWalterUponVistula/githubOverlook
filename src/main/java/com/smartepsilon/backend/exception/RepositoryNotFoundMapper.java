package com.smartepsilon.backend.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RepositoryNotFoundMapper implements ExceptionMapper<RepositoryNotFound> {
	
	public Response toResponse(RepositoryNotFound exception) {
		return Response.status(Response.Status.NOT_FOUND)
				       .entity(exception.getMessage())
				       .build();
	}
}
