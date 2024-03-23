package com.ssh.dartserver.domain.vote.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.List;

@Embeddable
@NoArgsConstructor
@Getter
public class Candidates {
    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL)
    private List<Candidate> values;

    public Candidates(List<Candidate> values) {
        validateCandidatesSize(values);
        this.values = values;
    }

    public static Candidates of(List<Candidate> values) {
        return new Candidates(values);
    }

    private void validateCandidatesSize(List<Candidate> values) {
        if (values == null || values.size() != 4) {
            throw new IllegalArgumentException("후보자는 반드시 4명이어야 합니다.");
        }
    }

}
