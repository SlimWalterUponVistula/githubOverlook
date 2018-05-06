package com.smartepsilon.gitrepo.resource;

import com.smartepsilon.gitrepo.representation.GithubRepositoryRto;

public interface GithubRepositoryResource {

	GithubRepositoryRto readByOwnerAndName(String owner, String name);
}
