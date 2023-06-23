package com.ssh.dartserver.user.domain;

import com.ssh.dartserver.common.BaseTimeEntity;
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
public class User extends BaseTimeEntity {
    @Id @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    private String name;

    private String phone;

    private String sex;

    private String password;

    @Column(name = "admission_num")
    private String admissionNum; //학번

    @Enumerated(EnumType.STRING)
    private Role role;

    private String point;

    //== social login ==//
    @Column(unique = true)
    private String username;
    private String providerId;
    private String provider;

}
