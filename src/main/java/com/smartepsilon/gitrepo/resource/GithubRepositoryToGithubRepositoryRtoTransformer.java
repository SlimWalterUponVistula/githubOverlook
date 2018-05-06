package com.smartepsilon.gitrepo.resource;

import java.util.function.Function;

import com.smartepsilon.gitrepo.model.GithubRepository;
import com.smartepsilon.gitrepo.representation.GithubRepositoryRto;

class GithubRepositoryToGithubRepositoryRtoTransformer implements Function<GithubRepository, GithubRepositoryRto> {

    static Function<GithubRepository, GithubRepositoryRto> INSTANCE = new GithubRepositoryToGithubRepositoryRtoTransformer();

    @Override
    public GithubRepositoryRto apply(GithubRepository githubRepository) {
        return GithubRepositoryRto.builder().withFullName(githubRepository.getName())
                .withDescription(githubRepository.getDescription()).withCloneUrl(githubRepository.getCloneUrl())
                .withStars(String.valueOf(githubRepository.getStargazersCount()))
                .withCreatedAt(githubRepository.getCreationDate()).build();
    }
}
