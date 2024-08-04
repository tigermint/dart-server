package com.ssh.dartserver.domain.university.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@BatchSize(size = 500)
public class University {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "university_id")
    private Long id;
    private String name;
    private String department;
    private String area;

    @Builder
    private University(
            final Long id,
            final String name,
            final String department,
            final String area
    ) {
        this.id = id;
        this.area = area;
        this.name = name;
        this.department = department;
    }
}
