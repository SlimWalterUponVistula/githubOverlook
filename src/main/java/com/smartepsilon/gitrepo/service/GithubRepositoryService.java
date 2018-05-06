package com.smartepsilon.gitrepo.service;

import com.smartepsilon.gitrepo.model.GithubRepository;

public interface GithubRepositoryService {

	GithubRepository readByOwnerAndRepositoryName(String repositoryOwner, String repositoryName);
}
