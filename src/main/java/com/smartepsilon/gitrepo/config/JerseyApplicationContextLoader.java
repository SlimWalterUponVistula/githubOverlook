package com.smartepsilon.gitrepo.config;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

import com.smartepsilon.backend.exception.ExternalServiceUnhealthyMapper;
import com.smartepsilon.backend.exception.RepositoryNotFoundMapper;
import com.smartepsilon.gitrepo.resource.GithubRepositoryResourceImpl;

@ApplicationPath("/")
public class JerseyApplicationContextLoader extends ResourceConfig {

	public JerseyApplicationContextLoader() {
		super();
		register(RepositoryNotFoundMapper.class);
		register(ExternalServiceUnhealthyMapper.class);
		register(GithubRepositoryResourceImpl.class);
	}
}
