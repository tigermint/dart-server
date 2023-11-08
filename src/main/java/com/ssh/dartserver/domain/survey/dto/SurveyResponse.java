package com.ssh.dartserver.domain.survey.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class SurveyResponse {
    private SurveyResponse() {
        throw new IllegalStateException("Utility class");
    }
    @Data
    public static class ReadDto {
        private Long surveyId;
        private LocalDateTime createdTime;
        private LocalDateTime lastModifiedTime;
        private String category;
        private String content;
        private Integer totalHeadCount;
        private List<AnswerDto> answers;
        private Long userAnswerId;
        private List<CommentDto> comments;

        @Data
        public static class CommentDto {
            private Long commentId;
            private LocalDateTime createdTime;
            private LocalDateTime lastModifiedTime;
            private String content;
            private Integer like;
            private Boolean isLiked;
            private Boolean isReported;
            private UserDto user;
        }

        @Data
        public static class UserDto {
            private Long userId;
            private String name;
            private String nickname;
            private String gender;
            private Integer admissionYear;
            private UniversityDto university;
        }

        @Data
        public static class UniversityDto {
            private Long universityId;
            private String name;
            private String department;
        }

    }

    @Data
    public static class ListDto {
        private Long surveyId;
        private LocalDateTime createdTime;
        private LocalDateTime lastModifiedTime;
        private String category;
        private String content;
        private Integer totalHeadCount;
        private List<AnswerDto> answers;
        private Long userAnswerId;
        private String latestComment;

    }

    @Data
    public static class AnswerDto {
        private Long answerId;
        private String content;
        private Integer headCount;
    }
}
