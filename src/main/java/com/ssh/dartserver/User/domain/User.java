package com.ssh.dartserver.User.domain;

import com.ssh.dartserver.common.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id @GeneratedValue
    private Long id;

    private String name;

    @Column(unique = true)
    private String username;

    private String email;

    private String password;

    private String providerId;

    private String provider;

    @Enumerated(EnumType.STRING)
    private Role role;
}
