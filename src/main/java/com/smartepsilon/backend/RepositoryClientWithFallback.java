package com.smartepsilon.backend;

import javax.ws.rs.core.Response;

public interface RepositoryClientWithFallback {

	Response getRepository(String owner, String id);
}
