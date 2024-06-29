package com.ssh.dartserver.domain.university.infra;

import com.ssh.dartserver.domain.university.domain.University;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UniversityRepository extends CrudRepository<University, Long> {
    @Query(value = "SELECT u.name FROM University u WHERE u.name LIKE :universityName% GROUP BY u.name ORDER BY u.name ASC LIMIT 20", nativeQuery = true)
    List<String> findTop20ByNameStartsWith(@NonNull @Param("universityName") String universityName);
    List<University> findDistinctTop20ByNameAndDepartmentStartsWith(@NonNull String name, @NonNull String department);
}
