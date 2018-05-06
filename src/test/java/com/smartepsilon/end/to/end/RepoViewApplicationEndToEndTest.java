package com.smartepsilon.end.to.end;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.stream.IntStream;

import javax.ws.rs.client.WebTarget;

import org.testng.annotations.Test;

import com.google.common.base.Stopwatch;
import com.smartepsilon.gitrepo.config.JerseyTestNG;
import com.smartepsilon.gitrepo.representation.GithubRepositoryRto;

public class RepoViewApplicationEndToEndTest extends JerseyTestNG {

	private static final int REQUESTS_COUNT = 30;
	private static final String RESOURCE_PATH_TEMPLATE = "/repositories/%s/%s";
	
	private static final int SECOND_MILIS = 1000;
	private static final String OWNER = "mhahsler";
	private static final String REPO_NAME = "TSP";
	private static final String EXPECTED_SAMPLE_DESCRIPTION = "Traveling Salesperson Problem - R package";
	
	@Test
	public void shouldProvideSingleRepositoryInfoWithMockedExternalEndpoint() throws Exception {
		// given
		WebTarget target = target(resolveUri());
		// when
		GithubRepositoryRto githubRepositoryRto = target.request().get(GithubRepositoryRto.class);		
		// then
		assertThat(githubRepositoryRto).isNotNull();
		assertThat(githubRepositoryRto.getStars()).isEqualTo("777");
		assertThat(githubRepositoryRto.getFullName()).isEqualTo("LS83_MOCK_mhahsler/TSP");
		assertThat(githubRepositoryRto.getCloneUrl()).isEqualTo("https://github.com/mhahsler/TSP.git");
	}
	
	@Test
	public void shouldRunManyRequestsConcurrentlyAndMeasureExecutionTimeBelowOneSecond() throws Exception {
		// given
		WebTarget target = target(resolveUri());
		Stopwatch st = Stopwatch.createStarted();
		// when
		IntStream.range(0, REQUESTS_COUNT).parallel().forEach(i -> {
			GithubRepositoryRto githubRepositoryRto = target.request().get(GithubRepositoryRto.class);
			assertThat(githubRepositoryRto).isNotNull();
			assertThat(githubRepositoryRto.getDescription()).isEqualTo(EXPECTED_SAMPLE_DESCRIPTION);
		});
		// then
		assertThat(st.elapsed()).isLessThanOrEqualTo(Duration.ofMillis(SECOND_MILIS));
	}

	private String resolveUri() {
		return resolve(RESOURCE_PATH_TEMPLATE, OWNER, REPO_NAME);
	}
}
