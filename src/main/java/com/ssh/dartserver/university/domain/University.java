package com.ssh.dartserver.university.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class University {
    @Id @GeneratedValue
    @Column(name = "university_id")
    private Long id;

    private String area;

    private String name;

    @Column(name = "campus_type")
    private String type;

    private String department;

    private String state;

    private String div0;

    private String div1;

    private String div2;

    private String div3;

    private String years;

}