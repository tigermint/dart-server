package com.ssh.dartserver.domain.team.v2;

import static org.assertj.core.api.Assertions.assertThat;

import com.ssh.dartserver.ApiTest;
import com.ssh.dartserver.TestRepository;
import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.personalinfo.Gender;
import com.ssh.dartserver.testing.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
class BlindDateTeamRepositoryImplTest extends ApiTest {

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private BlindDateTeamRepository blindDateTeamRepositoryImpl;

    @Test
    @DisplayName("pageSize와 조회할 데이터 수가 같은 경우 성공적으로 데이터를 반환한다.")
    @Transactional
    void returnsCorrectDataWhenPageSizeMatchesDataCount() {
        setNormalData();

        University university = testRepository.findUniversityByName("인천대학교", "컴퓨터공학과");
        User currentUser = testRepository.createUser("testUser", Gender.FEMALE, university);
        Pageable pageable = PageRequest.of(0, 4);

        Page<Team> result = blindDateTeamRepositoryImpl.findAll(currentUser, pageable);  // <- 페이징 쿼리 및 카운트 쿼리 2개 발생
        assertThat(result.getContent()).hasSize(4);
        assertThat(result.getTotalElements()).isEqualTo(4);
        assertThat(result.getTotalPages()).isEqualTo(1);
    }

    @Test
    @DisplayName("pageSize가 전체 데이터 중 일부인 경우 성공적으로 데이터를 반환한다.")
    @Transactional
    void returnsCorrectDataWhenPageSizeIsPartOfTotalData() {
        setNormalData();

        University university = testRepository.findUniversityByName("서울대학교", "일어일문학과");
        User currentUser = testRepository.createUser("testUser", Gender.FEMALE, university);
        Pageable pageable = PageRequest.of(0, 1);  // 데이터 일부만 요청

        Page<Team> result = blindDateTeamRepositoryImpl.findAll(currentUser, pageable);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(4);
        assertThat(result.getTotalPages()).isEqualTo(4);
    }

    @Test
    @DisplayName("offset이 범위를 초과한 경우 빈 리스트를 반환한다.")
    @Transactional
    void returnsEmptyListWhenOffsetExceedsDataRange() {
        setNormalData();

        University university = testRepository.findUniversityByName("서울대학교", "일어일문학과");
        User currentUser = testRepository.createUser("testUser", Gender.MALE, university);
        Pageable pageable = PageRequest.of(100, 2);  // offset이 데이터를 초과

        Page<Team> result = blindDateTeamRepositoryImpl.findAll(currentUser, pageable);
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("pageSize가 전체 데이터 수보다 큰 경우 성공적으로 데이터를 반환한다.")
    @Transactional
    void returnsAllDataWhenPageSizeExceedsTotalDataCount() {
        setNormalData();

        University university = testRepository.findUniversityByName("인천대학교", "컴퓨터공학과");
        User currentUser = testRepository.createUser("testUser", Gender.FEMALE, university);
        Pageable pageable = PageRequest.of(0, 10);  // pageSize가 전체 데이터보다 큼

        Page<Team> result = blindDateTeamRepositoryImpl.findAll(currentUser, pageable);
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent()).hasSizeLessThanOrEqualTo(10);  // 데이터를 모두 반환
    }

    @Test
    @DisplayName("반대 성별에 해당하는 데이터가 없는 경우 빈 리스트를 반환한다.")
    @Transactional
    void returnsEmptyListWhenNoOppositeGenderDataExists() {
        setFemaleAndSameUniversityData();

        University university = testRepository.findUniversityByName("인천대학교", "컴퓨터공학과");
        User currentUser = testRepository.createUser("testUser", Gender.FEMALE, university);
        Pageable pageable = PageRequest.of(0, 10);

        Page<Team> result = blindDateTeamRepositoryImpl.findAll(currentUser, pageable);
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("모든 사용자가 같은 학교 일 때, 같은 학교에 노출하지 않기 옵션을 모두 켰다면 빈 리스트를 반환한다.")
    @Transactional
    void returnsEmptyListWhenSameUniversityOptionIsDisabledForAll() {
        setFemaleAndSameUniversityData();

        University university = testRepository.findUniversityByName("인천대학교", "컴퓨터공학과");
        User currentUser = testRepository.createUser("testUser", Gender.MALE, university);
        Pageable pageable = PageRequest.of(0, 10);

        Page<Team> result = blindDateTeamRepositoryImpl.findAll(currentUser, pageable);
        assertThat(result.getContent()).isEmpty();
    }

    private void setNormalData() {
        // 학교 데이터 생성
        University uni1 = testRepository.addUniversity("인천대학교", "컴퓨터공학과");
        University uni2 = testRepository.addUniversity("서울대학교", "일어일문학과");

        // 사용자 데이터 생성 (성별과 학교가 다른 8명의 사용자)
        User maleUser1 = testRepository.createUser("maleUser1", Gender.MALE, uni1);
        User femaleUser1 = testRepository.createUser("femaleUser1", Gender.FEMALE, uni1);
        User maleUser2 = testRepository.createUser("maleUser2", Gender.MALE, uni1);
        User femaleUser2 = testRepository.createUser("femaleUser2", Gender.FEMALE, uni1);
        User maleUser3 = testRepository.createUser("maleUser3", Gender.MALE, uni2);
        User femaleUser3 = testRepository.createUser("femaleUser3", Gender.FEMALE, uni2);
        User maleUser4 = testRepository.createUser("maleUser4", Gender.MALE, uni2);
        User femaleUser4 = testRepository.createUser("femaleUser4", Gender.FEMALE, uni2);

        // 지역 데이터 생성
        testRepository.addRegion("인천");
        testRepository.addRegion("서울");

        // 팀 데이터 생성 (각기 다른 Boolean 값과 지역 조합을 가지도록 설정)
        testRepository.createTeam("Team 1", maleUser1, Boolean.TRUE, "인천");    // TRUE, 인천
        testRepository.createTeam("Team 2", femaleUser1, Boolean.FALSE, "인천");  // FALSE, 인천
        testRepository.createTeam("Team 3", maleUser2, Boolean.TRUE, "서울");     // TRUE, 서울
        testRepository.createTeam("Team 4", femaleUser2, Boolean.FALSE, "서울");  // FALSE, 서울
        testRepository.createTeam("Team 5", maleUser3, Boolean.TRUE, "인천");     // TRUE, 인천
        testRepository.createTeam("Team 6", femaleUser3, Boolean.FALSE, "인천");  // FALSE, 인천
        testRepository.createTeam("Team 7", maleUser4, Boolean.TRUE, "서울");     // TRUE, 서울
        testRepository.createTeam("Team 8", femaleUser4, Boolean.FALSE, "서울");  // FALSE, 서울

        // 팀에 이미지 및 지역 설정
        testRepository.addTeamImage("Team 1", "https://www.naver.com/image1.jpg");
        testRepository.addTeamImage("Team 2", "https://www.naver.com/image2.jpg");
        testRepository.addTeamImage("Team 3", "https://www.naver.com/image3.jpg");
        testRepository.addTeamImage("Team 4", "https://www.naver.com/image4.jpg");
        testRepository.addTeamImage("Team 5", "https://www.naver.com/image5.jpg");
        testRepository.addTeamImage("Team 6", "https://www.naver.com/image6.jpg");
        testRepository.addTeamImage("Team 7", "https://www.naver.com/image7.jpg");
        testRepository.addTeamImage("Team 8", "https://www.naver.com/image8.jpg");

        testRepository.addTeamRegion("Team 1", "인천");
        testRepository.addTeamRegion("Team 2", "인천");
        testRepository.addTeamRegion("Team 3", "서울");
        testRepository.addTeamRegion("Team 4", "서울");
        testRepository.addTeamRegion("Team 5", "인천");
        testRepository.addTeamRegion("Team 6", "인천");
        testRepository.addTeamRegion("Team 7", "서울");
        testRepository.addTeamRegion("Team 8", "서울");
    }

    private void setFemaleAndSameUniversityData() {
        // 학교 데이터 생성
        University uni1 = testRepository.addUniversity("인천대학교", "컴퓨터공학과");
        University uni2 = testRepository.addUniversity("서울대학교", "일어일문학과");

        // 사용자 데이터 생성 (성별과 학교가 다른 8명의 사용자)
        User femaleUser1 = testRepository.createUser("femaleUser1", Gender.FEMALE, uni1);
        User femaleUser2 = testRepository.createUser("femaleUser2", Gender.FEMALE, uni1);
        User femaleUser3 = testRepository.createUser("femaleUser3", Gender.FEMALE, uni1);
        User femaleUser4 = testRepository.createUser("femaleUser4", Gender.FEMALE, uni1);

        // 지역 데이터 생성
        testRepository.addRegion("인천");
        testRepository.addRegion("서울");

        // 팀 데이터 생성
        testRepository.createTeam("Team 1", femaleUser1, Boolean.FALSE, "인천");
        testRepository.createTeam("Team 2", femaleUser2, Boolean.FALSE, "인천");
        testRepository.createTeam("Team 3", femaleUser3, Boolean.FALSE, "서울");
        testRepository.createTeam("Team 4", femaleUser4, Boolean.FALSE, "서울");

        // 팀에 이미지 및 지역 설정
        testRepository.addTeamImage("Team 1", "https://www.naver.com/image1.jpg");
        testRepository.addTeamImage("Team 2", "https://www.naver.com/image2.jpg");
        testRepository.addTeamImage("Team 3", "https://www.naver.com/image3.jpg");
        testRepository.addTeamImage("Team 4", "https://www.naver.com/image4.jpg");

        testRepository.addTeamRegion("Team 1", "인천");
        testRepository.addTeamRegion("Team 2", "인천");
        testRepository.addTeamRegion("Team 3", "서울");
        testRepository.addTeamRegion("Team 4", "서울");
    }

}
