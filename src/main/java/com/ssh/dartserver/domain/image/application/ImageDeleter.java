package com.ssh.dartserver.domain.image.application;

import com.ssh.dartserver.domain.image.infra.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageDeleter {
    private final ImageRepository imageRepository;

    // 이미지 삭제
    public void deleteImage(Long id) {
        if (imageRepository.existsById(id)) {
            imageRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Image not found with id: " + id);
        }
    }

}
