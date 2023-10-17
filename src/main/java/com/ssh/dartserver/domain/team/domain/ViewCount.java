package com.ssh.dartserver.domain.team.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor
public class ViewCount {
    private static final int VIEW_COUNT_INCREMENT = 1;

    @Column(name = "view_count")
    private int value;

    private ViewCount(int value) {
        this.value = value;
    }

    public static ViewCount from(int value) {
        return new ViewCount(value);
    }

    public ViewCount increase() {
        return new ViewCount(this.value + VIEW_COUNT_INCREMENT);
    }
}
