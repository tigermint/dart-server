package com.ssh.dartserver.friend.service;

import com.ssh.dartserver.friend.domain.Friend;
import com.ssh.dartserver.friend.dto.FriendRequestDto;
import com.ssh.dartserver.friend.dto.FriendResponseDto;
import com.ssh.dartserver.friend.infra.mapper.FriendMapper;
import com.ssh.dartserver.friend.infra.persistence.FriendRepository;
import com.ssh.dartserver.user.domain.User;
import com.ssh.dartserver.user.infra.persistence.UserRepository;
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

    @Transactional
    public void create(User user, FriendRequestDto request) {
        Friend friend = Friend.builder()
                .friendUserId(request.getFriendUserId())
                .user(user)
                .build();
        friendRepository.save(friend);
    }

    public List<FriendResponseDto> list(User user) {
        List<Friend> friends = friendRepository.findAllByUserId(user.getId());
        List<FriendResponseDto> dtos = new ArrayList<>();

        friends.forEach(friend -> {
            User friendUserInfo = userRepository.findById(friend.getFriendUserId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
            dtos.add(friendMapper.toFriendResponseDto(friendUserInfo));
        });

        return dtos;
    }

    @Transactional
    public void delete(User user, Long friendUserId) {
        Friend friend = friendRepository.findByUserIdAndFriendUserId(user.getId(), friendUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 친구입니다."));
        friendRepository.deleteById(friend.getId());
    }

    /**
     * 친구의 친구 및 같은 학교 학과 친구 목록을 반환한다.
     * @param user
     * @return
     */
    public List<FriendResponseDto> possibleList(User user) {
        List<FriendResponseDto> dtos = new ArrayList<>();

        List<User> friendsOfFriends = friendRepository.findAllFriendsOfFriendsById(user.getId()).stream()
                .map(userRepository::findById)
                .filter(Optional::isPresent)
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


        possibleUsersWithoutFriends.forEach(possibleUser -> dtos.add(friendMapper.toFriendResponseDto(possibleUser)));
        return dtos;
    }
}
