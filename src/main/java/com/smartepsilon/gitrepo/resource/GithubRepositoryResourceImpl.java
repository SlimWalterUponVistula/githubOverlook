package com.smartepsilon.gitrepo.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;

import com.smartepsilon.gitrepo.model.GithubRepository;
import com.smartepsilon.gitrepo.representation.GithubRepositoryRto;
import com.smartepsilon.gitrepo.service.GithubRepositoryService;

@Path(GithubRepositoryResourceImpl.BASE_PATH)
public class GithubRepositoryResourceImpl implements GithubRepositoryResource {

    static final String BASE_PATH = "/repositories";

    private final GithubRepositoryService githubRepositoryService;

    @Autowired
    public GithubRepositoryResourceImpl(GithubRepositoryService githubRepositoryService) {
        this.githubRepositoryService = githubRepositoryService;
    }

    @GET
    @Path("/{owner}/{repository-name}")
    @Produces(MediaType.APPLICATION_JSON)
    public GithubRepositoryRto readByOwnerAndName(@PathParam("owner") String owner,
                                                  @PathParam("repository-name") String repositoryName) {
        GithubRepository readRepository = githubRepositoryService.readByOwnerAndRepositoryName(owner, repositoryName);
        return GithubRepositoryToGithubRepositoryRtoTransformer.INSTANCE.apply(readRepository);
    }
}
