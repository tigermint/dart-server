package com.ssh.dartserver.domain.university.domain;

import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Builder
    private University(
            final Long id,
            final String area,
            final String name,
            final String type,
            final String department,
            final String state,
            final String div0,
            final String div1,
            final String div2,
            final String div3,
            final String years
    ) {
        this.id = id;
        this.area = area;
        this.name = name;
        this.type = type;
        this.department = department;
        this.state = state;
        this.div0 = div0;
        this.div1 = div1;
        this.div2 = div2;
        this.div3 = div3;
        this.years = years;
    }
}
