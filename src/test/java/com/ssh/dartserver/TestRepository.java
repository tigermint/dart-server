package com.ssh.dartserver;

import com.ssh.dartserver.domain.image.application.ImageUploader;
import com.ssh.dartserver.domain.image.domain.Image;
import com.ssh.dartserver.domain.team.domain.Region;
import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.domain.TeamImage;
import com.ssh.dartserver.domain.team.domain.TeamRegion;
import com.ssh.dartserver.domain.team.domain.vo.TeamDescription;
import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.user.domain.AuthInfo;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.personalinfo.BirthYear;
import com.ssh.dartserver.domain.user.domain.personalinfo.Gender;
import com.ssh.dartserver.domain.user.domain.personalinfo.PersonalInfo;
import com.ssh.dartserver.domain.user.domain.studentverificationinfo.StudentVerificationInfo;
import com.ssh.dartserver.global.common.Role;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TestRepository {

    @Autowired
    EntityManager entityManager;

    @Autowired
    ImageUploader imageUploader;

    public User createUser(String name, Gender gender, University university) {
        User user = User.builder()
                .authInfo(AuthInfo.of(name, "kakao", "12345678_" + name))
                .personalInfo(PersonalInfo.builder()
                        .birthYear(BirthYear.from(2000))
                        .gender(gender)
                        .build())
                .university(university)
                .studentVerificationInfo(StudentVerificationInfo.newInstance())
                .role(Role.USER)
                .build();

        entityManager.persist(user);
        return user;
    }

    public Team createTeam(String name, User leader, Boolean isVisibleToSameUniversity, String regionName) {
        Team team = Team.builder()
                .user(leader)
                .name(name)
                .description(new TeamDescription("팀 설명입니다."))
                .isVisibleToSameUniversity(isVisibleToSameUniversity)
                .build();
        entityManager.persist(team);

        TeamRegion teamRegion = addTeamRegion(name, regionName);
        TeamImage teamImage = addTeamImage(name, "https://www.naver.com/image1.jpg");

        team.setTeamRegions(List.of(teamRegion));
        team.setTeamImages(List.of(teamImage));
        entityManager.persist(team);
        return team;
    }

    public University addUniversity(String name, String department) {
        University university = University.builder()
                .name(name)
                .department(department)
                .area("AREA")
                .build();

        entityManager.persist(university);
        return university;
    }

    public TeamImage addTeamImage(String teamName, String imageUrl) {
        Team team = findTeamByName(teamName);
        Image image = imageUploader.saveImageUrl(imageUrl);

        TeamImage teamImage = new TeamImage(null, team, image);
        entityManager.persist(image);

        return teamImage;
    }

    public TeamRegion addTeamRegion(String teamName, String regionName) {
        Team team = findTeamByName(teamName);
        Region region = findRegionByName(regionName);
        TeamRegion teamRegion = new TeamRegion(null, team, region);
        entityManager.persist(teamRegion);

        return teamRegion;
    }

    public Region addRegion(String regionName) {
        Region region = Region.builder()
                .name(regionName)
                .build();
        entityManager.persist(region);

        return region;
    }

    public Team findTeamByName(String name) {
        return entityManager.createQuery("SELECT t FROM Team t WHERE t.name.value = :name", Team.class)
                .setParameter("name", name)
                .getSingleResult();
    }

    public Region findRegionByName(String name) {
        return entityManager.createQuery("SELECT r FROM Region r WHERE r.name = :name", Region.class)
                .setParameter("name", name)
                .getSingleResult();
    }

    public University findUniversityByName(String name, String department) {
        return entityManager.createQuery(
                        "SELECT u FROM University u WHERE u.name = :name and u.department = :department", University.class)
                .setParameter("name", name)
                .setParameter("department", department)
                .getSingleResult();
    }

}