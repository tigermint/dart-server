package com.ssh.dartserver.domain.chat.dto.mapper;

import com.ssh.dartserver.domain.chat.domain.ChatRoom;
import com.ssh.dartserver.domain.chat.dto.ChatRoomResponse;
import com.ssh.dartserver.domain.question.domain.Question;
import com.ssh.dartserver.domain.team.domain.Region;
import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.profilequestions.ProfileQuestion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatRoomMapper {
    //Read
    @Mapping(target = "chatRoomId", source = "chatRoom.id")
    @Mapping(target = "latestChatMessageContent", source = "chatRoom.latestChatMessageContent")
    @Mapping(target = "latestChatMessageTime", source = "chatRoom.latestChatMessageTime")
    @Mapping(target = "requestingTeam", source = "requestingTeam")
    @Mapping(target = "requestedTeam", source = "requestedTeam")
    ChatRoomResponse.ReadDto toReadDto(ChatRoom chatRoom, ChatRoomResponse.ReadDto.TeamDto requestingTeam, ChatRoomResponse.ReadDto.TeamDto requestedTeam);

    @Mapping(target = "teamId", source = "team.id")
    @Mapping(target = "name", source = "team.name.value")
    @Mapping(target = "isStudentIdCardVerified", source = "isStudentIdCardVerified")
    @Mapping(target = "university", source = "team.university")
    @Mapping(target = "teamUsers", source = "teamUsers")
    @Mapping(target = "teamRegions", source = "teamRegions")
    ChatRoomResponse.ReadDto.TeamDto toReadTeamDto(Team team, Boolean isStudentIdCardVerified, List<ChatRoomResponse.ReadDto.UserDto> teamUsers, List<ChatRoomResponse.ReadDto.RegionDto> teamRegions);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "nickname", source = "user.personalInfo.nickname.value")
    @Mapping(target = "birthYear", source = "user.personalInfo.birthYear.value")
    @Mapping(target = "profileImageUrl", source = "user.personalInfo.profileImageUrl.value")
    @Mapping(target = "university", source = "university")
    @Mapping(target = "profileQuestions", source = "profileQuestions")
    ChatRoomResponse.ReadDto.UserDto toReadUserDto(User user, ChatRoomResponse.ReadDto.UniversityDto university, List<ChatRoomResponse.ReadDto.ProfileQuestionDto> profileQuestions);

    @Mapping(target = "universityId", source = "university.id")
    @Mapping(target = "name", source = "university.name")
    @Mapping(target = "department", source = "university.department")
    ChatRoomResponse.ReadDto.UniversityDto toReadUniversityDto(University university);

    @Mapping(target = "regionId", source = "region.id")
    @Mapping(target = "name", source = "region.name")
    ChatRoomResponse.ReadDto.RegionDto toReadRegionDto(Region region);

    @Mapping(target = "profileQuestionId", source = "profileQuestion.id")
    @Mapping(target = "count", source = "profileQuestion.count")
    @Mapping(target = "question", source = "questionDto")
    ChatRoomResponse.ReadDto.ProfileQuestionDto toReadProfileQuestionDto(ProfileQuestion profileQuestion, ChatRoomResponse.ReadDto.QuestionDto questionDto);

    @Mapping(target = "questionId", source = "question.id")
    @Mapping(target = "content", source = "question.content")
    ChatRoomResponse.ReadDto.QuestionDto toReadQuestionDto(Question question);

    //List
    @Mapping(target = "chatRoomId", source = "chatRoom.id")
    @Mapping(target = "latestChatMessageContent", source = "chatRoom.latestChatMessageContent")
    @Mapping(target = "latestChatMessageTime", source = "chatRoom.latestChatMessageTime")
    @Mapping(target = "requestingTeam", source = "requestingTeam")
    @Mapping(target = "requestedTeam", source = "requestedTeam")
    ChatRoomResponse.ListDto toListDto(ChatRoom chatRoom, ChatRoomResponse.ListDto.TeamDto requestingTeam, ChatRoomResponse.ListDto.TeamDto requestedTeam);
    @Mapping(target = "teamId", source = "team.id")
    @Mapping(target = "name", source = "team.name.value")
    @Mapping(target = "isStudentIdCardVerified", source = "isStudentIdCardVerified")
    @Mapping(target = "university", source = "university")
    @Mapping(target = "teamUsers", source = "teamUsers")
    @Mapping(target = "teamRegions", source = "teamRegions")
    ChatRoomResponse.ListDto.TeamDto toListTeamDto(Team team, Boolean isStudentIdCardVerified, ChatRoomResponse.ListDto.UniversityDto university ,List<ChatRoomResponse.ListDto.UserDto> teamUsers, List<ChatRoomResponse.ListDto.RegionDto> teamRegions);


    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "profileImageUrl", source = "user.personalInfo.profileImageUrl.value")
    ChatRoomResponse.ListDto.UserDto toListUserDto(User user);

    @Mapping(target = "universityId", source = "university.id")
    @Mapping(target = "name", source = "university.name")
    ChatRoomResponse.ListDto.UniversityDto toListUniversityDto(University university);

    @Mapping(target = "regionId", source = "region.id")
    @Mapping(target = "name", source = "region.name")
    ChatRoomResponse.ListDto.RegionDto toListRegionDto(Region region);

}
