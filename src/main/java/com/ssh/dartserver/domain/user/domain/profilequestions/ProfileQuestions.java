package com.ssh.dartserver.domain.user.domain.profilequestions;


import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.List;

@Embeddable
@NoArgsConstructor
@Getter
public class ProfileQuestions {

    @OneToMany(mappedBy = "user")
    List<ProfileQuestion> values;

    public ProfileQuestions(List<ProfileQuestion> values) {
        validateProfileQuestionsSize(values);
        this.values = values;
    }
    public static ProfileQuestions newInstance() {
        return new ProfileQuestions();
    }

    public static ProfileQuestions of(List<ProfileQuestion> values) {
        return new ProfileQuestions(values);
    }

    private void validateProfileQuestionsSize(List<ProfileQuestion> values) {
        if (values.size() > 3) {
            throw new IllegalArgumentException("프로필 질문은 3개 이하여야 합니다.");
        }
    }


}
