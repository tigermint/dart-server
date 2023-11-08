package com.ssh.dartserver.domain.survey.domain;

import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.global.common.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Comment extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Embedded
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Survey survey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @OneToMany(mappedBy = "comment")
    private List<CommentLike> commentLikes = new ArrayList<>();

    @OneToMany(mappedBy = "comment")
    private List<CommentReport> commentReports = new ArrayList<>();
}
