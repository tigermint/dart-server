package com.ssh.dartserver.domain.university.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import jakarta.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@BatchSize(size = 500)
public class University {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
