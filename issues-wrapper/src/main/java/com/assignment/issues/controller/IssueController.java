// Author: Salih Eren Yuzbazzozlu
package com.assignment.issues.controller;

import com.assignment.issues.model.Comment;
import com.assignment.issues.model.CommentCreateRequest;
import com.assignment.issues.model.Issue;
import com.assignment.issues.model.IssueCreateRequest;
import com.assignment.issues.model.IssueUpdateRequest;
import com.assignment.issues.service.GitHubService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/issues")
public class IssueController {

    private final GitHubService gitHubService;

    public IssueController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @PostMapping
    public Mono<ResponseEntity<Issue>> createIssue(@Valid @RequestBody IssueCreateRequest request) {
        return gitHubService.createIssue(request)
                .map(githubResponse -> {
                    Issue issue = githubResponse.getBody();
                    if (issue != null) {
                        return ResponseEntity.status(HttpStatus.CREATED)
                                .header(HttpHeaders.LOCATION, "/issues/" + issue.getNumber())
                                .body(issue);
                    }
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }

    @GetMapping
    public Mono<ResponseEntity<List<Issue>>> listIssues(
            @RequestParam(required = false, defaultValue = "open") String state,
            @RequestParam(required = false) List<String> labels,
            @RequestParam(required = false) Integer page,
            @RequestParam(name = "per_page", required = false) Integer perPage) {
        
        return gitHubService.listIssues(state, labels, page, perPage)
                .map(githubResponse -> {
                    ResponseEntity.BodyBuilder builder = ResponseEntity.ok();
                    // Propagate Link header for pagination
                    List<String> linkHeader = githubResponse.getHeaders().get(HttpHeaders.LINK);
                    if (linkHeader != null && !linkHeader.isEmpty()) {
                        builder.header(HttpHeaders.LINK, linkHeader.toArray(new String[0]));
                    }
                    return builder.body(githubResponse.getBody());
                });
    }

    @GetMapping("/{number}")
    public Mono<ResponseEntity<Issue>> getIssue(@PathVariable int number) {
        return gitHubService.getIssue(number)
                .map(githubResponse -> ResponseEntity.ok(githubResponse.getBody()));
    }

    @PatchMapping("/{number}")
    public Mono<ResponseEntity<Issue>> updateIssue(
            @PathVariable int number,
            @RequestBody IssueUpdateRequest request) {
        return gitHubService.updateIssue(number, request)
                .map(githubResponse -> ResponseEntity.ok(githubResponse.getBody()));
    }

    @PostMapping("/{number}/comments")
    public Mono<ResponseEntity<Comment>> createComment(
            @PathVariable int number,
            @Valid @RequestBody CommentCreateRequest request) {
        return gitHubService.createComment(number, request)
                .map(githubResponse -> ResponseEntity.status(HttpStatus.CREATED).body(githubResponse.getBody()));
    }
}
