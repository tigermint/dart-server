package com.ssh.dartserver.domain.university.infra;

import com.ssh.dartserver.domain.university.domain.University;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UniversityRepository extends JpaRepository<University, Long> {
    @Override
    List<University> findAll(Sort sort);
}
