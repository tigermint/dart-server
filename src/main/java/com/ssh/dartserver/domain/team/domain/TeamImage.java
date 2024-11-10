package com.ssh.dartserver.domain.team.domain;

import com.ssh.dartserver.domain.image.domain.Image;
import com.ssh.dartserver.global.common.BaseTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@Table(name = "team_image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamImage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_image_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Image image;

    @Builder
    public TeamImage(Long id, Team team, Image image) {
        this.id = id;
        this.team = team;
        this.image = image;
    }

    public TeamImage(Team team, Image image) {
        this.team = team;
        this.image = image;
    }

    @Override
    public String toString() {
        return "TeamImage{" +
                "id=" + id +
                ", teamId=" + team.getId() +
                ", imageId=" + image.getId() +
                '}';
    }
}
