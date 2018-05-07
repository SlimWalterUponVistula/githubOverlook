package com.smartepsilon.gitrepo.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.google.gson.annotations.SerializedName;

public class GithubRepository implements Serializable {

    private static final long serialVersionUID = 945188191706944161L;

    @SerializedName("full_name")
    private String name;

    private String description;

    @SerializedName("clone_url")
    private String cloneUrl;

    @SerializedName("stargazers_count")
    private int stargazersCount;

    @SerializedName("created_at")
    private LocalDateTime creationDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCloneUrl() {
        return cloneUrl;
    }

    public void setCloneUrl(String cloneUrl) {
        this.cloneUrl = cloneUrl;
    }

    public int getStargazersCount() {
        return stargazersCount;
    }

    public void setStargazersCount(int stargazersCount) {
        this.stargazersCount = stargazersCount;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private GithubRepository repository = new GithubRepository();
        
        public Builder withName(String name) {
            repository.setName(name);
            return this;
        }
        
        public GithubRepository build() {
            return repository;
        }
    }
}
