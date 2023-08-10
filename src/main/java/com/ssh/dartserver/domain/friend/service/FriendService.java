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
import com.ssh.dartserver.domain.user.dto.mapper.UserMapper;
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
    public void createFriendById(User user, FriendRequest request) {
        User friendUser = userRepository.findById(request.getFriendUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        if(isFriend(user, friendUser)){
            throw new IllegalArgumentException("이미 친구입니다.");
        }
        save(user, friendUser);
    }

    @Transactional
    public FriendResponse createFriendByRecommendationCode(User user, FriendRecommendationCodeRequest request) {
        User friendUser = userRepository.findByRecommendationCode(new RecommendationCode(request.getRecommendationCode()))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 추천인 코드입니다."));
        if(isFriend(user, friendUser)){
            throw new IllegalArgumentException("이미 친구입니다.");
        }
        save(user, friendUser);
        return getFriendResponseDto(friendUser);
    }

    public List<FriendResponse> listFriend(User user) {
        List<Friend> friends = friendRepository.findAllByUserId(user.getId());
        List<FriendResponse> friendResponses = new ArrayList<>();
        friends.forEach(friend -> friendResponses.add(getFriendResponseDto(friend.getUser())));
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
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 친구입니다."));
        friendRepository.deleteById(friend.getId());
    }

    private boolean isFriend(User user, User friendUser) {
        return friendRepository.findByUserIdAndFriendUserId(user.getId(), friendUser.getId())
                .isPresent();
    }

    private void save(User user, User friendUser) {
        Friend friend = Friend.builder()
                .friendUser(friendUser)
                .user(user)
                .build();
        friendRepository.save(friend);
    }

    private FriendResponse getFriendResponseDto(User friend) {
        return friendMapper.toFriendResponseDto(
                userMapper.toUserResponseDto(friend), universityMapper.toUniversityResponseDto(friend.getUniversity())
        );
    }

}
