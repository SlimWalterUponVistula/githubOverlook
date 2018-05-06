package com.smartepsilon.cross.components;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import javax.ws.rs.client.WebTarget;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Stopwatch;
import com.smartepsilon.backend.GithhubRepositoryClient;
import com.smartepsilon.backend.GithubRepositoryBackend;
import com.smartepsilon.backend.GithubRepositoryBackendImpl;
import com.smartepsilon.backend.RepositoryClientWithFallback;
import com.smartepsilon.backend.RepositoryClientWithFallbackImpl;
import com.smartepsilon.backend.exception.RepositoryNotFound;
import com.smartepsilon.backend.util.WebTargetFactory;
import com.smartepsilon.gitrepo.representation.GithubRepositoryRto;
import com.smartepsilon.gitrepo.resource.GithubRepositoryResource;
import com.smartepsilon.gitrepo.resource.GithubRepositoryResourceImpl;
import com.smartepsilon.gitrepo.service.GithubRepositoryService;
import com.smartepsilon.gitrepo.service.GithubRepositoryServiceImpl;

public class RepoViewApplicationComponentsIntegrationTest {

	private static final String EXPECTED_TSP_FULL_NAME = "mhahsler/TSP";

	private static final String EXPECTED_TSP_STARS = "17";

	private static final String EXPECTED_TSP_CLONE_URL = "https://github.com/mhahsler/TSP.git";

	private static final String EXPECTED_CREATED_AT = "2015-10-10T02:54:22Z";

	private static final long TOLERANCE_MILIS = 6000;

	private static final int PARALLEL_RUNS_COUNT = 20;
	
	private static final String REPO_OWNER = "mhahsler";
	
	private static final String REPO_ID = "TSP";
	
	private static final String GITHUB_RESTFUL_ENDPOINT_URI = "https://api.github.com/repos/{owner}/{id}";

	private static final String EXPECTED_TSP_REPO_DESCRIPTION = "Traveling Salesperson Problem - R package";

	private static final String MESSAGE_TEMPLATE_FOR_NONEXISTENT_REPO = "Repository with given owner [%s] and id [%s] has not been found!";

	private static final long TIMEOUT = 2000;

	private static final int RETRIES = 4;
	
	private GithubRepositoryResource testee;
	
	@BeforeMethod
	public void prepareResource() {
		GithhubRepositoryClient githubRepositoryClient = new GithhubRepositoryClient(repositoryClientWithFallback());
		GithubRepositoryBackend githubRepositoryBackend = new GithubRepositoryBackendImpl(githubRepositoryClient );
		GithubRepositoryService githubRepositoryService = new GithubRepositoryServiceImpl(githubRepositoryBackend);
		testee = new GithubRepositoryResourceImpl(githubRepositoryService);
	}

	private RepositoryClientWithFallback repositoryClientWithFallback() {
		WebTarget webTarget = WebTargetFactory.create(GITHUB_RESTFUL_ENDPOINT_URI);
		return new RepositoryClientWithFallbackImpl(webTarget,
				                                    webTarget,
				                                    TIMEOUT, 
				                                    RETRIES);
	}
	
	@Test
	public void shouldFindGithubTspRepository() {
		// given
		// when
		GithubRepositoryRto repoFound = testee.readByOwnerAndName(REPO_OWNER, REPO_ID);
		
		// then
		assertNotNull(repoFound);
		assertEquals(repoFound.getDescription(), EXPECTED_TSP_REPO_DESCRIPTION);
		assertEquals(repoFound.getFullName(), EXPECTED_TSP_FULL_NAME);
		assertEquals(repoFound.getStars(), EXPECTED_TSP_STARS);
		assertEquals(repoFound.getCloneUrl(), EXPECTED_TSP_CLONE_URL);
		assertEquals(repoFound.getCreatedAt(), EXPECTED_CREATED_AT);
	}
	
	@Test
	public void shouldRaiseRepositoryNotFoundOnInvalidOwner() {
		// given
		final String nonExistentId = "009112eroifFDSFdggf98320947859k3244fdgfdXXXXYYY";
		final String repoId = "TSP";
		
		assertThatExceptionOfType(RepositoryNotFound.class)
		    .isThrownBy(() -> testee.readByOwnerAndName(nonExistentId, REPO_ID))
		    .withMessage(String.format(MESSAGE_TEMPLATE_FOR_NONEXISTENT_REPO, nonExistentId, repoId));
	}

	@Test
	public void shouldRunTwentyRealRequestsInParallelWithinBoundedTime() {
		// given
		// when
		Stopwatch sw = Stopwatch.createStarted();

		IntStream.range(0, PARALLEL_RUNS_COUNT).parallel().forEach(i -> {
		    testee.readByOwnerAndName(REPO_OWNER, REPO_ID);
		});
		// then
		long elapsed = sw.elapsed(TimeUnit.MILLISECONDS);
		Assert.assertTrue(elapsed <= TOLERANCE_MILIS, String.format("Time taken [%d]", elapsed));
	}
}
