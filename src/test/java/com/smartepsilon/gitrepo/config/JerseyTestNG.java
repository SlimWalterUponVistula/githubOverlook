package com.smartepsilon.gitrepo.config;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.Application;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.smartepsilon.gitrepo.config.JerseyApplicationContextLoader;

public class JerseyTestNG extends JerseyTest {

	private static final Integer SUCCESS_CODE = 200;

	private static final Integer MOCK_SERVER_PORT = 8085;
	
	private static final String SAMPLE_JSON_RESPONSE = "/tspResponse.json";
	
	private static final String TSP_REPO_PREPARED_PATH = "/repos/mhahsler/TSP";
	
	private ClientAndServer mockServer;
	
	@BeforeMethod
	public void beforeMethod() throws Exception {
		this.setUp();
		this.mockServer = startClientAndServer(MOCK_SERVER_PORT);
		mockExpectedResponseWhenApplicationTriesToFetchRepositoryByOwnerAndName();
	}
	
	@AfterMethod
	public void afterMethod() throws Exception {
		this.tearDown();
		this.mockServer.stop();
	}
	
	@Override
	protected Application configure() {
		ResourceConfig resourceConfig = new JerseyApplicationContextLoader();
		ApplicationContext annotationBasedConfig = new AnnotationConfigApplicationContext(RepoViewApplicationConfig.class);
		resourceConfig.property("contextConfig", annotationBasedConfig);
		return resourceConfig;
	}
	
	protected void mockExpectedResponseWhenApplicationTriesToFetchRepositoryByOwnerAndName() throws IOException {
		new MockServerClient("localhost", MOCK_SERVER_PORT)
				.when(
						request()
				            .withMethod(HttpMethod.GET.name())
						    .withPath(TSP_REPO_PREPARED_PATH)) 
				.respond(
						response()
						    .withBody(getSampleJsonBody())
						    .withStatusCode(SUCCESS_CODE));
	}

	protected String resolve(String resourcePath, String owner, String repoName) {
		return String.format(resourcePath, owner, repoName);
	}
	
	private String getSampleJsonBody() throws IOException {
		InputStream resourceAsStream = this.getClass().getResourceAsStream(SAMPLE_JSON_RESPONSE);
		return IOUtils.toString(resourceAsStream);
	}
}
