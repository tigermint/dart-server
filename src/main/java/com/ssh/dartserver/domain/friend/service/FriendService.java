package com.ssh.dartserver.domain.friend.service;

import com.ssh.dartserver.domain.friend.domain.Friend;
import com.ssh.dartserver.domain.friend.dto.FriendRecommendationCodeRequest;
import com.ssh.dartserver.domain.friend.dto.FriendRequest;
import com.ssh.dartserver.domain.friend.dto.FriendResponse;
import com.ssh.dartserver.domain.friend.dto.mapper.FriendMapper;
import com.ssh.dartserver.domain.friend.infra.FriendRepository;
import com.ssh.dartserver.domain.university.dto.mapper.UniversityMapper;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.recommendcode.RecommendationCode;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final FriendMapper friendMapper;
    private final UniversityMapper universityMapper;

    @Transactional
    public void createFriendById(User user, FriendRequest request) {
        isFriend(user.getId(), request.getFriendUserId());
        save(request.getFriendUserId(), user);
    }

    @Transactional
    public FriendResponse createFriendByRecommendationCode(User user, FriendRecommendationCodeRequest request) {
        User friendUser = userRepository.findByRecommendationCode(new RecommendationCode(request.getRecommendationCode()))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 추천인 코드입니다."));
        isFriend(user.getId(), friendUser.getId());
        save(friendUser.getId(), user);
        return friendMapper.toFriendResponseDto(friendUser, universityMapper.toUniversityResponseDto(friendUser.getUniversity()));
    }


    public List<FriendResponse> listFriend(User user) {
        List<Friend> friends = friendRepository.findAllByUserId(user.getId());
        List<FriendResponse> dtos = new ArrayList<>();
        friends.forEach(friend -> {
            User friendUserInfo = userRepository.findById(friend.getFriendUserId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
            dtos.add(friendMapper.toFriendResponseDto(friendUserInfo,
                    universityMapper.toUniversityResponseDto(friendUserInfo.getUniversity())));
        });
        return dtos;
    }

    public List<FriendResponse> listPossibleFriend(User user) {
        List<FriendResponse> dtos = new ArrayList<>();

        List<User> friendsOfFriends = friendRepository.findAllFriendsOfFriendsById(user.getId()).stream()
                .map(userRepository::findById)
                .filter(Optional::isPresent)
                .filter(friend -> !friend.get().getId().equals(user.getId()))
                .map(Optional::get)
                .collect(Collectors.toList());

        List<User> sameDepartmentUsers = userRepository.findAllByUniversityId(user.getUniversity().getId()).stream()
                .filter(departmentUser -> !departmentUser.getId().equals(user.getId()))
                .collect(Collectors.toList());

        List<User> possibleUsers = Stream.concat(friendsOfFriends.stream(), sameDepartmentUsers.stream())
                .distinct()
                .collect(Collectors.toList());


        List<User> friends = friendRepository.findAllByUserId(user.getId()).stream()
                .map(friend -> userRepository.findById(friend.getFriendUserId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        List<User> possibleUsersWithoutFriends = possibleUsers.stream()
                .filter(possibleUser -> friends.stream().noneMatch(friend -> friend.getId().equals(possibleUser.getId())))
                .collect(Collectors.toList());

        possibleUsersWithoutFriends.forEach(possibleUser -> dtos.add(
                friendMapper.toFriendResponseDto(possibleUser, universityMapper.toUniversityResponseDto(possibleUser.getUniversity()))));
        return dtos;
    }

    @Transactional
    public void delete(User user, Long friendUserId) {
        Friend friend = friendRepository.findByUserIdAndFriendUserId(user.getId(), friendUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 친구입니다."));
        friendRepository.deleteById(friend.getId());
    }


    private void save(Long friendUser, User user) {
        Friend friend = Friend.builder()
                .friendUserId(friendUser)
                .user(user)
                .build();
        friendRepository.save(friend);
    }

    private void isFriend(Long userId, Long friendUserId) {
        if(userRepository.findById(friendUserId).isEmpty()){
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }
        if(friendRepository.findByUserIdAndFriendUserId(userId, friendUserId).isPresent()){
            throw new IllegalArgumentException("이미 친구 관계인 유저입니다.");
        }
    }
}
