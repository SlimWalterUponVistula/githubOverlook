package com.smartepsilon.backend;

import static com.smartepsilon.backend.util.GsonDeserializer.extractEntityAndUnmarshal;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.smartepsilon.backend.exception.RepositoryNotFound;
import com.smartepsilon.gitrepo.model.GithubRepository;

public class GithhubRepositoryClient implements RepositoryClient {

    private final RepositoryClientWithFallback webTargetWithFallback;

    public GithhubRepositoryClient(RepositoryClientWithFallback webTargetWithFallback) {
        this.webTargetWithFallback = webTargetWithFallback;
    }

    public GithubRepository getRepository(String owner, String id) {
        try (Response response = webTargetWithFallback.getRepository(owner, id)) {
            if (statusNotFound(response)) {
                throw new RepositoryNotFound(owner, id);
            }
            return extractEntityAndUnmarshal(response, GithubRepository.class);
        }
    }

    private boolean statusNotFound(Response response) {
        return response.getStatusInfo().getStatusCode() == Status.NOT_FOUND.getStatusCode();
    }
}
