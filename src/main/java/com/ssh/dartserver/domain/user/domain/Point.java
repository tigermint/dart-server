package com.ssh.dartserver.domain.user.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor
public class Point {
    @Column(name = "point")
    public int value;

    public Point(int value) {
        this.value = value;
    }

    public static Point from(int value) {
        return new Point(value);
    }
}