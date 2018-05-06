package com.smartepsilon.gitrepo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.smartepsilon.backend.GithhubRepositoryClient;
import com.smartepsilon.backend.GithubRepositoryBackend;
import com.smartepsilon.backend.GithubRepositoryBackendImpl;
import com.smartepsilon.backend.RepositoryClient;
import com.smartepsilon.backend.RepositoryClientWithFallback;
import com.smartepsilon.backend.RepositoryClientWithFallbackImpl;
import com.smartepsilon.backend.util.WebTargetFactory;
import com.smartepsilon.gitrepo.service.GithubRepositoryService;
import com.smartepsilon.gitrepo.service.GithubRepositoryServiceImpl;

@Configuration
@ComponentScan(RepoViewSpringBasedConfig.ROOT_PACKAGE_COMPONENT_SCAN)
@PropertySource(RepoViewSpringBasedConfig.CLASSPATH_PROPERTIES_LOCATION)
public class RepoViewSpringBasedConfig {
	
	static final String ROOT_PACKAGE_COMPONENT_SCAN = "com.smartepsilon.*";
	
	static final String CLASSPATH_PROPERTIES_LOCATION = "classpath:externals/rest.properties";
	
	static final String GITHUB_REST_URI_PROPERTY_KEY = "com.smartepsilon.backend.gitrepo.read.uri";

	private static final String REQUEST_TIMEOUT_MILIS = "com.smartepsilon.backend.timeout";

	private static final String REQUEST_RETRIES_LIMIT = "com.smartepsilon.backend.retries";

	private static final String FALLBACK_URI_PROPERTY_KEY = "com.smartepsilon.backend.fallback.uri";

	@Autowired
	private Environment environment;
	
	@Bean
	public GithubRepositoryBackend githubRepositoryBackend() {
		return new GithubRepositoryBackendImpl(githhubRepositoryClient());
	}
	
	@Bean
	public RepositoryClient githhubRepositoryClient() {
		return new GithhubRepositoryClient(repositoryClientWithFallback());
	}
	
	@Bean
	public RepositoryClientWithFallback repositoryClientWithFallback() {
		String restUri = environment.getProperty(GITHUB_REST_URI_PROPERTY_KEY);
		// the next line is for conceptual purposes, should know real redirect uri for circuit breaker
		String fallbackUriOnServiceUnavailability = environment.getProperty(FALLBACK_URI_PROPERTY_KEY, restUri);
		String timeout = environment.getProperty(REQUEST_TIMEOUT_MILIS);
		String retriesLimit = environment.getProperty(REQUEST_RETRIES_LIMIT);
		return new RepositoryClientWithFallbackImpl(WebTargetFactory.create(restUri),
				                                    WebTargetFactory.create(fallbackUriOnServiceUnavailability),                                    
				                                    Long.valueOf(timeout), 
				                                    Integer.valueOf(retriesLimit));
	}
	
	@Bean
	public GithubRepositoryService githubRepositoryService() {
		return new GithubRepositoryServiceImpl(githubRepositoryBackend());
	}
}
