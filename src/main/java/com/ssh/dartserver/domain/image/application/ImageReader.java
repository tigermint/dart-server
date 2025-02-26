package com.ssh.dartserver.domain.image.application;

import com.ssh.dartserver.domain.image.domain.Image;
import com.ssh.dartserver.domain.image.infra.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageReader {
    private final ImageRepository imageRepository;

    // 이미지 정보 조회 (ID로 조회)
    public Image getImageById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Image not found with id: " + id));
    }

}
