package com.ssh.dartserver.domain.proposal.presentation.response;

import com.ssh.dartserver.domain.user.domain.studentverificationinfo.StudentIdCardVerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class ProposalResponse {
    private ProposalResponse() {
        throw new IllegalStateException("Utility class");
    }

    @Data
    public static class ListDto {
        private Long proposalId;
        private LocalDateTime createdTime;
        private TeamDto requestingTeam;
        private TeamDto requestedTeam;

        @Data
        @Builder
        public static class TeamDto {
            private Long teamId;
            private String name;
            private double averageAge;
            private List<UserDto> users;
            private List<RegionDto> regions;
        }

        @Data
        @Builder
        public static class UserDto {
            private Long userId;
            private String nickname;
            private int birthYear;
            private StudentIdCardVerificationStatus studentIdCardVerificationStatus;
            private String profileImageUrl;
            private UniversityDto university;
        }

        @Data
        @AllArgsConstructor
        public static class RegionDto {
            private Long regionId;
            private String name;
        }

        @Data
        @AllArgsConstructor
        public static class UniversityDto {
            private Long universityId;
            private String name;
            private String department;
        }
    }

    @Data
    public static class UpdateDto{
        private Long proposalId;
        private String proposalStatus;
        private Long requestingTeamId;
        private Long requestedTeamId;
    }
}
