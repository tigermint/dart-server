package com.ssh.dartserver.domain.friend.application;

import com.ssh.dartserver.domain.friend.domain.Friend;
import com.ssh.dartserver.domain.friend.presentation.request.FriendRecommendationCodeRequest;
import com.ssh.dartserver.domain.friend.presentation.request.FriendRequest;
import com.ssh.dartserver.domain.friend.presentation.response.FriendResponse;
import com.ssh.dartserver.domain.friend.infra.FriendRepository;
import com.ssh.dartserver.domain.university.application.UniversityMapper;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.RecommendationCode;
import com.ssh.dartserver.domain.user.application.UserMapper;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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
    private final UserMapper userMapper;

    @Transactional
    public Long createFriendById(User user, FriendRequest request) {
        User friendUser = userRepository.findById(request.getFriendUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        validateIsAlreadyFriend(user, friendUser);

        Friend savedFriend = save(user, friendUser);
        return savedFriend.getId();
    }


    @Transactional
    public Long createFriendByRecommendationCode(User user, FriendRecommendationCodeRequest request) {
        User friendUser = userRepository.findByRecommendationCode(RecommendationCode.from(request.getRecommendationCode()))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 추천인 코드입니다."));

        validateIsAlreadyFriend(user, friendUser);

        Friend savedFriend = save(user, friendUser);
        return savedFriend.getId();
    }

    public FriendResponse readFriend(User user, Long friendId) {
        Friend friend = friendRepository.findByUserIdAndId(user.getId(), friendId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 친구관계 입니다."));
        return getFriendResponseDto(friend.getFriendUser());
    }

    public List<FriendResponse> listFriend(User user) {
        List<Friend> friends = friendRepository.findAllByUserId(user.getId());
        List<FriendResponse> friendResponses = new ArrayList<>();
        friends.forEach(friend -> friendResponses.add(getFriendResponseDto(friend.getFriendUser())));
        return friendResponses;
    }


    public List<FriendResponse> listPossibleFriend(User user) {
        List<FriendResponse> friendResponses = new ArrayList<>();
        List<User> addedMeAsFriendUsers = userRepository.findAllAddedMeAsFriendByUserId(user.getId());
        List<User> sameDepartmentUsers = userRepository.findAllSameDepartmentByUniversityId(user.getUniversity().getId(), user.getId());
        List<User> friendsOfFriendUsers = userRepository.findAllFriendsOfFriendsByUserId(user.getId());
        List<User> friendUsers = friendRepository.findAllByUserId(user.getId()).stream()
                .map(Friend::getFriendUser)
                .collect(Collectors.toList());

        List<User> possibleFriends = Stream.of(addedMeAsFriendUsers, sameDepartmentUsers, friendsOfFriendUsers)
                .flatMap(List::stream)
                .distinct()
                .filter(friend -> !friendUsers.contains(friend))
                .collect(Collectors.toList());

        possibleFriends.forEach(friend -> friendResponses.add(getFriendResponseDto(friend)));
        return friendResponses;
    }

    @Transactional
    public void delete(User user, Long friendUserId) {
        Friend friend = friendRepository.findByUserIdAndFriendUserId(user.getId(), friendUserId)
                .orElseThrow(() -> new IllegalArgumentException("나와 친구 관계가 아닌 유저입니다."));
        friendRepository.deleteById(friend.getId());
    }

    private Friend save(User user, User friendUser) {
        Friend friend = Friend.builder()
                .friendUser(friendUser)
                .user(user)
                .build();
        return friendRepository.save(friend);
    }

    private FriendResponse getFriendResponseDto(User friend) {
        return friendMapper.toFriendResponseDto(
                userMapper.toUserResponse(friend), universityMapper.toUniversityResponse(friend.getUniversity())
        );
    }
    private void validateIsAlreadyFriend(User user, User friendUser) {
        friendRepository.findByUserIdAndFriendUserId(user.getId(), friendUser.getId()).ifPresent(friend -> {
            throw new IllegalArgumentException("이미 친구입니다.");
        });
    }

}
