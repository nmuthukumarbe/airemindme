package com.server.realsync.dto;

public class TemplateGenerateResponse {
    private String content;

    public TemplateGenerateResponse() {}

    public TemplateGenerateResponse(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
