package com.ssh.dartserver.domain.image.application;

import com.ssh.dartserver.domain.image.domain.Image;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageUploader imageUploader;
    private final ImageDeleter imageDeleter;
    private final ImageReader imageReader;

    // 단일 이미지 URL 등록
    public Image saveImageUrl(String imageUrl) {
        return imageUploader.saveImageUrl(imageUrl);
    }

    // 여러 이미지 URL 등록
    public List<Image> saveImageUrls(List<String> imageUrls) {
        return imageUploader.saveImageUrls(imageUrls);
    }

    // 이미지 정보 조회 (ID로 조회)
    public Image getImageById(Long id) {
        return imageReader.getImageById(id);
    }

    // 이미지 삭제
    public void deleteImage(Long id) {
        imageDeleter.deleteImage(id);
    }

}
