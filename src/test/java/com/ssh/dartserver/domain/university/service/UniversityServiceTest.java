package com.ssh.dartserver.domain.university.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.university.dto.UniversityResponse;
import com.ssh.dartserver.domain.university.dto.UniversitySearchRequest;
import com.ssh.dartserver.domain.university.dto.mapper.UniversityMapperImpl;
import com.ssh.dartserver.domain.university.infra.UniversityRepository;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class UniversityServiceTest {
    @InjectMocks
    private UniversityService universityService;
    @Mock
    private UniversityRepository universityRepository;

    @BeforeEach
    void setup() {
        universityService = new UniversityService(universityRepository, new UniversityMapperImpl());
    }

    @ParameterizedTest
    @DisplayName("학교명으로 검색한다")
    @MethodSource("keywordAndResultProvider")
    void searchUniversity(String keyword, List<String> result) {
        // given
        int size = 10;
        UniversitySearchRequest request = new UniversitySearchRequest();
        request.setName(keyword);
        request.setSize(10);

        Mockito.when(universityRepository.findAllByNameStartsWith(request.getName(), 10)).thenReturn(result);

        // when
        final List<UniversityResponse> response = universityService.search(request);

        // then
        assertThat(response).hasSize(result.size());
    }

    private static Stream<Arguments> keywordAndResultProvider() {
        return Stream.of(
            // 검색 키워드, 검색 반환 리스트
            Arguments.arguments("인천", List.of("인천대학교", "인천국제대학교")),
            Arguments.arguments("서울", List.of("서울대학교")),
            Arguments.arguments("검색결과없음", List.of())
        );
    }

    @Test
    @DisplayName("학교 학과명으로 검색한다")
    void searchNameAndDepartment() {
        // given
        int size = 20;
        UniversitySearchRequest request = new UniversitySearchRequest();
        request.setName("인천대학교");
        request.setDepartment("사회");
        request.setSize(size);

        Mockito.when(universityRepository.findDistinctByNameAndDepartmentStartsWith(request.getName(), request.getDepartment(),
            PageRequest.of(0, size, Sort.by("id").ascending()))).thenReturn(
            List.of(
                new University(1L, "인천대학교", "사회과", "인천"),
                new University(2L, "인천대학교", "사회사회과", "인천"),
                new University(3L, "인천대학교", "사회과학과", "인천"),
                new University(4L, "인천대학교", "사회환원과", "인천")
            )
        );

        // when
        final List<UniversityResponse> result = universityService.search(request);

        // then
        assertThat(result).hasSize(4);
        assertThat(result.get(0).getName()).isEqualTo("인천대학교");
        assertThat(result.get(0).getDepartment()).startsWith("사회");
    }
}
