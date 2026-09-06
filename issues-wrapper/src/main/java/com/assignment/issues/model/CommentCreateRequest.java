// Author: Salih Eren Yüzbaşıoğlu
package com.assignment.issues.model;

import jakarta.validation.constraints.NotBlank;

public class CommentCreateRequest {

    @NotBlank(message = "Body cannot be blank")
    private String body;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
