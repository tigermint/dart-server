package com.ssh.dartserver.user.service;

import com.ssh.dartserver.university.domain.University;
import com.ssh.dartserver.university.infra.persistence.UniversityRepository;
import com.ssh.dartserver.user.domain.User;
import com.ssh.dartserver.user.dto.UserRequestDto;
import com.ssh.dartserver.user.dto.UserResponseDto;
import com.ssh.dartserver.user.infra.mapper.UserMapper;
import com.ssh.dartserver.user.infra.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UniversityRepository universityRepository;

    @Transactional
    public UserResponseDto create(User user, UserRequestDto userRequestDto) {
        validateDuplicationUser(user);
        University university = universityRepository.findById(userRequestDto.getUniversityId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대학교입니다."));
        user.update(university, userRequestDto);
        return userMapper.toResponseDto(userRepository.save(user));
    }

    public UserResponseDto read(User user) {
        return userMapper.toResponseDto(user);
    }
    @Transactional
    public UserResponseDto update(User user, UserRequestDto userRequestDto) {
        University university = universityRepository.findById(userRequestDto.getUniversityId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대학교입니다."));
        user.update(university, userRequestDto);
        return userMapper.toResponseDto(user);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }


    private void validateDuplicationUser(User user) {
        userRepository.findById(user.getId())
                .ifPresent(u -> {
                    throw new IllegalArgumentException("이미 가입된 회원입니다.");
                });
    }
}
