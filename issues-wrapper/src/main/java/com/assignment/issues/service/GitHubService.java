// Author: Salih Eren Yuzbazzozlu
package com.assignment.issues.service;

import com.assignment.issues.config.GitHubProperties;
import com.assignment.issues.model.Comment;
import com.assignment.issues.model.CommentCreateRequest;
import com.assignment.issues.model.Issue;
import com.assignment.issues.model.IssueCreateRequest;
import com.assignment.issues.model.IssueUpdateRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class GitHubService {

    private final WebClient webClient;
    private final GitHubProperties properties;

    public GitHubService(WebClient webClient, GitHubProperties properties) {
        this.webClient = webClient;
        this.properties = properties;
    }

    private String getBaseUri() {
        return "/repos/" + properties.getOwner() + "/" + properties.getRepo() + "/issues";
    }

    public Mono<ResponseEntity<Issue>> createIssue(IssueCreateRequest request) {
        return webClient.post()
                .uri(getBaseUri())
                .bodyValue(request)
                .retrieve()
                .toEntity(Issue.class);
    }

    public Mono<ResponseEntity<List<Issue>>> listIssues(String state, List<String> labels, Integer page, Integer perPage) {
        return webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path(getBaseUri());
                    if (state != null) uriBuilder.queryParam("state", state);
                    if (labels != null && !labels.isEmpty()) uriBuilder.queryParam("labels", String.join(",", labels));
                    if (page != null) uriBuilder.queryParam("page", page);
                    if (perPage != null) uriBuilder.queryParam("per_page", perPage);
                    return uriBuilder.build();
                })
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<Issue>>() {});
    }

    public Mono<ResponseEntity<Issue>> getIssue(int number) {
        return webClient.get()
                .uri(getBaseUri() + "/" + number)
                .retrieve()
                .toEntity(Issue.class);
    }

    public Mono<ResponseEntity<Issue>> updateIssue(int number, IssueUpdateRequest request) {
        return webClient.patch()
                .uri(getBaseUri() + "/" + number)
                .bodyValue(request)
                .retrieve()
                .toEntity(Issue.class);
    }

    public Mono<ResponseEntity<Comment>> createComment(int number, CommentCreateRequest request) {
        return webClient.post()
                .uri(getBaseUri() + "/" + number + "/comments")
                .bodyValue(request)
                .retrieve()
                .toEntity(Comment.class);
    }
}
