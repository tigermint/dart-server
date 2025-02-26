package com.ssh.dartserver.domain.user.domain.profilequestions;


import com.ssh.dartserver.domain.question.domain.Question;
import com.ssh.dartserver.domain.user.domain.User;
import lombok.*;

import jakarta.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "profile_question")
public class ProfileQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_question_id")
    private Long id;

    private Long count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Builder
    public ProfileQuestion(Long count, Question question, User user) {
        this.count = count;
        this.question = question;
        this.user = user;
    }
}
