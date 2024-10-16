package com.ssh.dartserver.domain.image;

import static org.junit.jupiter.api.Assertions.*;

import com.ssh.dartserver.ApiTest;
import com.ssh.dartserver.domain.image.application.ImageService;
import com.ssh.dartserver.domain.image.domain.Image;
import com.ssh.dartserver.domain.image.domain.ImageType;
import com.ssh.dartserver.domain.image.infra.ImageRepository;
import com.ssh.dartserver.testing.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@IntegrationTest
@Transactional
class ImageServiceTest extends ApiTest {

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageRepository imageRepository;

    @Nested
    @DisplayName("단일 이미지 URL 등록 테스트")
    class SaveImageUrl {

        @Test
        @DisplayName("정상적인 URL이 주어지면 이를 정상 등록한다.")
        void 정상적인_URL이_주어지면_정상_등록한다() {
            String imageUrl = "http://example.com/image.jpg";

            Image savedImage = imageService.saveImageUrl(imageUrl);

            assertNotNull(savedImage);
            assertEquals(imageUrl, savedImage.getData());
            assertEquals(ImageType.URL, savedImage.getType());
        }

        @Test
        @DisplayName("비정상적인 URL이 주어지면 예외를 발생시킨다.")
        void 비정상적인_URL이_주어지면_예외를_발생시킨다() {
            String invalidUrl = "invalid-url";

            assertThrows(IllegalArgumentException.class, () -> imageService.saveImageUrl(invalidUrl));
        }
    }

    @Nested
    @DisplayName("여러 이미지 URL 등록 테스트")
    class SaveImageUrls {

        @Test
        @DisplayName("빈 리스트가 주어지면 정상 수행한다.")
        void 빈_리스트가_주어지면_정상_수행한다() {
            List<String> emptyList = List.of();

            List<Image> savedImages = imageService.saveImageUrls(emptyList);

            assertTrue(savedImages.isEmpty());
        }

        @Test
        @DisplayName("정상적인 URL이 1개 들어오면 정상 등록한다.")
        void 정상적인_URL이_1개_들어오면_정상_등록한다() {
            List<String> urls = List.of("http://example.com/image1.jpg");

            List<Image> savedImages = imageService.saveImageUrls(urls);

            assertEquals(1, savedImages.size());
            assertEquals("http://example.com/image1.jpg", savedImages.get(0).getData());
        }

        @Test
        @DisplayName("정상적인 URL이 n개 들어오면 정상 등록한다.")
        void 정상적인_URL이_n개_들어오면_정상_등록한다() {
            List<String> urls = List.of("http://example.com/image1.jpg", "http://example.com/image2.jpg");

            List<Image> savedImages = imageService.saveImageUrls(urls);

            assertEquals(2, savedImages.size());
        }

        @Test
        @DisplayName("비정상적인 URL이 주어지면 예외를 발생시킨다.")
        void 비정상적인_URL이_주어지면_예외를_발생시킨다() {
            List<String> invalidUrls = List.of("invalid-url");

            assertThrows(IllegalArgumentException.class, () -> imageService.saveImageUrls(invalidUrls));
        }

        @Test
        @DisplayName("정상적인 URL과 비정상적인 URL이 함께 존재하면 예외를 발생시킨다.")
        void 정상적인_URL과_비정상적인_URL이_함께_존재하면_예외를_발생시킨다() {
            List<String> mixedUrls = List.of("http://example.com/image1.jpg", "invalid-url");

            assertThrows(IllegalArgumentException.class, () -> imageService.saveImageUrls(mixedUrls));
        }
    }

    @Nested
    @DisplayName("이미지 조회 테스트")
    class GetImageById {

        @Test
        @DisplayName("존재하는 아이디가 주어졌을 때 정상 조회된다.")
        void 존재하는_아이디가_주어졌을_때_정상_조회된다() {
            Image savedImage = imageService.saveImageUrl("http://example.com/image.jpg");

            Image foundImage = imageService.getImageById(savedImage.getId());

            assertNotNull(foundImage);
            assertEquals(savedImage.getId(), foundImage.getId());
        }

        @Test
        @DisplayName("존재하지 않는 아이디가 주어졌을 때 예외를 발생시킨다.")
        void 존재하지않는_아이디가_주어졌을_때_예외를_발생시킨다() {
            assertThrows(IllegalArgumentException.class, () -> imageService.getImageById(999L));
        }
    }

    @Nested
    @DisplayName("이미지 삭제 테스트")
    class DeleteImage {

        @Test
        @DisplayName("존재하는 아이디가 주어졌을 때 정상 삭제된다.")
        void 존재하는_아이디가_주어졌을_때_정상_삭제된다() {
            Image savedImage = imageService.saveImageUrl("http://example.com/image.jpg");

            imageService.deleteImage(savedImage.getId());

            assertFalse(imageRepository.existsById(savedImage.getId()));
        }

        @Test
        @DisplayName("존재하지 않는 아이디가 주어졌을 때 예외를 발생시킨다.")
        void 존재하지않는_아이디가_주어졌을_때_예외를_발생시킨다() {
            assertThrows(IllegalArgumentException.class, () -> imageService.deleteImage(999L));
        }
    }
}
