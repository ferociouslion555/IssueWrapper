// Author: Salih Eren Yuzbazzozlu
package com.assignment.issues.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public class Comment {

    private Long id;
    private String body;
    private User user;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @JsonProperty("html_url")
    private String htmlUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public static class User {
        private String login;
        private Long id;

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}
