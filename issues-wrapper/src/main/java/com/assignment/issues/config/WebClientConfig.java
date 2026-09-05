// Author: Salih Eren Yuzbazzozlu
package com.assignment.issues.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    private final GitHubProperties gitHubProperties;

    public WebClientConfig(GitHubProperties gitHubProperties) {
        this.gitHubProperties = gitHubProperties;
    }

    @Bean
    public WebClient gitHubWebClient(@org.springframework.beans.factory.annotation.Value("${github.api.url:https://api.github.com}") String githubApiUrl) {
        return WebClient.builder()
                .baseUrl(githubApiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + gitHubProperties.getToken())
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                .build();
    }
}
