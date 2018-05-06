package com.smartepsilon.gitrepo.representation;

import static java.time.format.DateTimeFormatter.ISO_INSTANT;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.base.MoreObjects;

@JsonInclude(Include.NON_NULL)
public class GithubRepositoryRto implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String fullName;
	private String description;
	private String cloneUrl;
	private String stars;
	private String createdAt;
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		private GithubRepositoryRto namedRepository = new GithubRepositoryRto();
		
		public Builder withFullName(String fullName) {
			namedRepository.fullName = fullName;
			return this;
		}

		public Builder withDescription(String description) {
			namedRepository.description = description;
			return this;
		}

		public Builder withCloneUrl(String cloneUrl) {
			namedRepository.cloneUrl = cloneUrl;
			return this;
		}

		public Builder withStars(String stars) {
			namedRepository.stars = stars;
			return this;
		}
		
		public Builder withCreatedAt(LocalDateTime creationTime) {
			if (creationTime != null) {
				namedRepository.createdAt = creationTime.atZone(ZoneOffset.UTC).format(ISO_INSTANT);
			}
			return this;
		}
		
		public GithubRepositoryRto build() {
			return namedRepository;
		}
	}

	public String getFullName() {
		return fullName;
	}

	public String getDescription() {
		return description;
	}

	public String getCloneUrl() {
		return cloneUrl;
	}

	public String getStars() {
		return stars;
	}

	public String getCreatedAt() {
		return createdAt;
	}
	
	@Override
	public String toString() {
		return
		MoreObjects.toStringHelper(this)
		.add("cloneUrl", this.cloneUrl)
		.add("description", this.description)
		.add("stars", this.getStars())
		.add("fullName", this.fullName)
		.add("createdAt", this.createdAt)
		.toString();
	}

}
