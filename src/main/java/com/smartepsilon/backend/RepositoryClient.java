package com.smartepsilon.backend;

import com.smartepsilon.gitrepo.model.GithubRepository;

public interface RepositoryClient {

	GithubRepository getRepository(String owner, String id);
}
