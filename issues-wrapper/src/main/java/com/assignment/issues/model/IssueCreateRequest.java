// Author: Salih Eren Yuzbazzozlu
package com.assignment.issues.model;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class IssueCreateRequest {

    @NotBlank(message = "Title cannot be blank")
    private String title;
    private String body;
    private List<String> labels;

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

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }
}
