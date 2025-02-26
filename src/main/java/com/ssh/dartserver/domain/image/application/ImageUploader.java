package com.ssh.dartserver.domain.image.application;

import com.ssh.dartserver.domain.image.domain.Image;
import com.ssh.dartserver.domain.image.infra.ImageRepository;
import com.ssh.dartserver.domain.image.domain.ImageType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ImageUploader {
    private final ImageRepository imageRepository;

    // 단일 이미지 URL 등록
    @Transactional
    public Image saveImageUrl(String imageUrl) {
        Image image = new Image(null, ImageType.URL, imageUrl);
        return imageRepository.save(image);
    }

    // 여러 이미지 URL 등록
    @Transactional
    public List<Image> saveImageUrls(List<String> imageUrls) {
        List<Image> images = imageUrls.stream()
                .map(url -> new Image(null, ImageType.URL, url))
                .toList();

        return imageRepository.saveAll(images);
    }

}
