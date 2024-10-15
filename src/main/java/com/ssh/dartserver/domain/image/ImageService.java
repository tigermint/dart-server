package com.ssh.dartserver.domain.image;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;

    // 단일 이미지 URL 등록
    public Image saveImageUrl(String imageUrl) {
        Image image = new Image(null, ImageType.URL, imageUrl);
        return imageRepository.save(image);
    }

    // 여러 이미지 URL 등록
    public List<Image> saveImageUrls(List<String> imageUrls) {
        List<Image> images = imageUrls.stream()
                .map(url -> new Image(null, ImageType.URL, url))
                .toList();

        return imageRepository.saveAll(images);
    }

    // 이미지 정보 조회 (ID로 조회)
    public Image getImageById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Image not found with id: " + id));
    }

    // 이미지 삭제
    public void deleteImage(Long id) {
        if (imageRepository.existsById(id)) {
            imageRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Image not found with id: " + id);
        }
    }

}
