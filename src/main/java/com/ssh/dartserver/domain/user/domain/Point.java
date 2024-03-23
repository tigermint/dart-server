package com.ssh.dartserver.domain.user.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor
public class Point {
    @Column(name = "point")
    public int value;

    private Point(int value) {
        this.value = value;
    }

    public static Point from(int value) {
        return new Point(value);
    }

    public Point add(int amount) {
        return new Point(this.value + amount);
    }

    public Point subtract(int amount) {
        validateRemainPoint(amount);
        return new Point(this.value - amount);
    }

    private void validateRemainPoint(int amount) {
        if (this.value - amount < 0) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }
    }
}
