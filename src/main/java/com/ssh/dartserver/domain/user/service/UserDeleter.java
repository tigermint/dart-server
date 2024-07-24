package com.ssh.dartserver.domain.user.service;

import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserDeleter {

    private final UserRepository userRepository;

    @Transactional
    public void delete(User user) {
        userRepository.delete(user);
    }
}
