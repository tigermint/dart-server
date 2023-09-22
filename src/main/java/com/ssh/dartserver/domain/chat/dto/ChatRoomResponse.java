package com.ssh.dartserver.domain.chat.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class ChatRoomResponse {
    private ChatRoomResponse() {
        throw new IllegalStateException("Utility class");
    }

    @Data
    public static class ReadDto {
        private Long chatRoomId;
        private String latestChatMessageContent;
        private LocalDateTime latestChatMessageTime;
        private TeamDto requestingTeam;
        private TeamDto requestedTeam;

        @Data
        public static class TeamDto{
            private Long teamId;
            private String name;
            private Boolean isStudentIdCardVerified;
            private UniversityDto university;
            private double averageAge;
            private List<UserDto> teamUsers;
            private List<RegionDto> teamRegions;
        }
        @Data
        public static class UserDto{
            private Long userId;
            private String nickname;
            private int birthYear;
//            private boolean isStudentIdCardVerified;
            private String profileImageUrl;
            private UniversityDto university;
            private List<ProfileQuestionDto> profileQuestions;
        }
        @Data
        public static class RegionDto{
            private Long regionId;
            private String name;
        }

        @Data
        public static class UniversityDto{
            private Long universityId;
            private String name;
            private String department;
        }

        @Data
        public static class ProfileQuestionDto{
            private Long profileQuestionId;
            private QuestionDto question;
            private int count;
        }

        @Data
        public static class QuestionDto{
            private Long questionId;
            private String content;
        }
    }

    @Data
    public static class ListDto {
        private Long chatRoomId;
        private String latestChatMessageContent;
        private LocalDateTime latestChatMessageTime;
        private TeamDto requestingTeam;
        private TeamDto requestedTeam;
        @Data
        public static class TeamDto {
            private Long teamId;
            private String name;
            private Boolean isStudentIdCardVerified;
            private UniversityDto university;
            private List<UserDto> teamUsers;
            private List<RegionDto> teamRegions;
        }
        @Data
        public static class UserDto{
            private Long userId;
            private String profileImageUrl;
        }
        @Data
        public static class RegionDto{
            private Long regionId;
            private String name;
        }

        @Data
        public static class UniversityDto{
            private Long universityId;
            private String name;
        }
    }

}
