package com.smartepsilon.backend;

import com.smartepsilon.gitrepo.model.GithubRepository;

public interface GithubRepositoryBackend {

    GithubRepository read(String owner, String name);
}
