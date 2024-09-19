package com.ssh.dartserver.domain.survey.domain;

import lombok.Getter;

import jakarta.persistence.*;

@Entity
@Getter
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    private String name;
}
