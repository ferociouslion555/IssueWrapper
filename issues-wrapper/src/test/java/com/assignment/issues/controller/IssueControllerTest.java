// Author: Salih Eren Yüzbaşıoğlu
package com.assignment.issues.controller;

import com.assignment.issues.model.Issue;
import com.assignment.issues.service.GitHubService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@WebFluxTest(IssueController.class)
class IssueControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private GitHubService gitHubService;

    @Test
    void testGetIssue() {
        Issue mockIssue = new Issue();
        mockIssue.setNumber(1);
        mockIssue.setTitle("Mock Issue");

        Mockito.when(gitHubService.getIssue(1))
                .thenReturn(Mono.just(ResponseEntity.ok(mockIssue)));

        webTestClient.get()
                .uri("/issues/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.number").isEqualTo(1)
                .jsonPath("$.title").isEqualTo("Mock Issue");
    }

    @Test
    void testListIssues() {
        Issue mockIssue = new Issue();
        mockIssue.setNumber(2);
        mockIssue.setTitle("List Mock Issue");

        Mockito.when(gitHubService.listIssues(any(), any(), any(), any()))
                .thenReturn(Mono.just(ResponseEntity.ok(List.of(mockIssue))));

        webTestClient.get()
                .uri("/issues?state=open")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].number").isEqualTo(2)
                .jsonPath("$[0].title").isEqualTo("List Mock Issue");
    }
}
