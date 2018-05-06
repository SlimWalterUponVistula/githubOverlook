package com.smartepsilon.gitrepo.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;

import com.smartepsilon.gitrepo.config.RepoViewSpringBasedConfig;

@Configuration
@ComponentScan(basePackageClasses = RepoViewSpringBasedConfig.class)
@TestPropertySource("classpath:externals/rest.properties")
public class RepoViewApplicationConfig {
}
