// Author: Salih Eren Yuzbazzozlu
package com.assignment.issues.model;

public class IssueUpdateRequest {

    private String title;
    private String body;
    private String state;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
