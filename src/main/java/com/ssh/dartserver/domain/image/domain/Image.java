package com.ssh.dartserver.domain.image.domain;

import com.ssh.dartserver.global.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends BaseTimeEntity {
    private static final Pattern URL_PATTERN =
            Pattern.compile("^(https?://|ftp://|www\\.)[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(/\\S*)?$");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ImageType type;

    @Column(name = "data", nullable = false)
    private String data;

    public boolean isImageUrlEqual(String imageUrl) {
        if (!type.isUrl()) {
            return false;
        }
        return data.equals(imageUrl);
    }

    public Image(Long id, ImageType type, String data) {
        validateData(type, data);

        this.id = id;
        this.type = type;
        this.data = data;
    }

    public Image(ImageType type, String data) {
        this(null, type, data);
    }

    private void validateData(ImageType type, String data) {
        // url 검증
        if (type.isUrl() && !URL_PATTERN.matcher(data).matches()) {
            throw new IllegalArgumentException("올바른 URL 형식이 아닙니다. data: " + data);
        }
    }

}
