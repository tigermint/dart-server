package com.ssh.dartserver.domain.team.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Embeddable
@Getter
@ToString
@NoArgsConstructor
public class TeamDescription {
    private static final int MAX_LENGTH = 50;

    @Column(name = "description")
    private String description;

    public TeamDescription(String description) {
        validateDescription(description);
        this.description = description;
    }

    private void validateDescription(String description) {
        if (description.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("팀 설명은 50글자를 넘을 수 없습니다. 현재길이: " + description.length());
        }
    }

}
