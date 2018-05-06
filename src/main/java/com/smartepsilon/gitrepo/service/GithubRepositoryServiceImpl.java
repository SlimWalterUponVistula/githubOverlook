package com.smartepsilon.gitrepo.service;

import com.smartepsilon.backend.GithubRepositoryBackend;
import com.smartepsilon.gitrepo.model.GithubRepository;

public class GithubRepositoryServiceImpl implements GithubRepositoryService {

    private final GithubRepositoryBackend githubRepositoryBackend;

    public GithubRepositoryServiceImpl(GithubRepositoryBackend githubRepositoryBackend) {
        this.githubRepositoryBackend = githubRepositoryBackend;
    }

    public GithubRepository readByOwnerAndRepositoryName(String owner, String repositoryName) {
        return githubRepositoryBackend.read(owner, repositoryName);
    }
}
