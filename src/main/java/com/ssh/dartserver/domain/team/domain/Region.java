package com.ssh.dartserver.domain.team.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "region_id")
    private Long id;
    private String name;
}
