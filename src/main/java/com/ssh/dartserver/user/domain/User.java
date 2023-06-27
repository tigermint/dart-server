package com.ssh.dartserver.user.domain;

import com.ssh.dartserver.common.BaseTimeEntity;
import com.ssh.dartserver.common.Role;
import com.ssh.dartserver.university.domain.University;
import com.ssh.dartserver.user.dto.UserRequestDto;
import lombok.*;

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
    private String gender;
    private String password;

    @Column(name = "admission_num")
    private int admissionNum; //학번

    @Enumerated(EnumType.STRING)
    private Role role;

    private String point;

    //== social login ==//
    //이름이 구려 굳이 필요 없다
    @Column(unique = true)
    private String username;

    private String providerId;
    private String provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private University university;

    /**
     * 유저 정보 수정
     * @param university
     * @param userRequestDto
     */
    public void update(University university, UserRequestDto userRequestDto) {
        this.university = university;
        this.admissionNum = userRequestDto.getAdmissionNum();
        this.name = userRequestDto.getName();
        this.phone = userRequestDto.getPhone();
    }
}