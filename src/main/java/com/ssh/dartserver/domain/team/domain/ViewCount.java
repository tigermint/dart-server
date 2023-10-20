package com.ssh.dartserver.domain.team.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor
public class ViewCount {

    @Column(name = "view_count")
    private int value;

    private ViewCount(int value) {
        this.value = value;
    }

    public static ViewCount from(int value) {
        return new ViewCount(value);
    }

    public ViewCount increase(int viewCountIncrement) {
        return new ViewCount(this.value + viewCountIncrement);
    }
}
