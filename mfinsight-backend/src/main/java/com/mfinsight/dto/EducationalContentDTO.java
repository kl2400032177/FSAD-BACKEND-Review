package com.mfinsight.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

public class EducationalContentDTO {

    @Data
    public static class CreateRequest {
        @NotBlank
        private String title;

        @NotBlank
        private String contentType;  // ARTICLE, VIDEO, INFOGRAPHIC, FAQ

        @NotBlank
        private String content;

        private String tags;

        @NotBlank
        private String targetAudience;  // BEGINNER, INTERMEDIATE, ADVANCED

        private boolean published = false;
    }

    @Data
    public static class UpdateRequest {
        private String title;
        private String contentType;
        private String content;
        private String tags;
        private String targetAudience;
        private Boolean published;
    }

    @Data
    public static class Response {
        private Long id;
        private String title;
        private String contentType;
        private String content;
        private String tags;
        private String targetAudience;
        private Long authorId;
        private String authorName;
        private boolean published;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
