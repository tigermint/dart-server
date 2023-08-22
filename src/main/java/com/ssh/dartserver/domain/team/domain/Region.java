package com.ssh.dartserver.domain.team.domain;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class Region {
    @Id @GeneratedValue
    @Column(name = "region_id")
    private Long id;
    private String name;
}
