package com.ssh.dartserver.domain.survey.domain;

import com.ssh.dartserver.global.common.BaseTimeEntity;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Survey extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Category category;

    @OneToMany(mappedBy = "survey")
    private List<Answer> answers = new ArrayList<>();

    @OneToMany(mappedBy = "survey")
    private List<Comment> comments = new ArrayList<>();
}
