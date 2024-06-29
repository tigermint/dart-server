package com.ssh.dartserver.domain.university.infra;

import com.ssh.dartserver.domain.university.domain.University;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UniversityRepository extends CrudRepository<University, Long> {
    @Query(value = "SELECT u.name FROM University u WHERE u.name LIKE :universityName% GROUP BY u.name ORDER BY u.name ASC LIMIT :size", nativeQuery = true)
    List<String> findAllByNameStartsWith(@NonNull @Param("universityName") String universityName, @Param("size") int size);
    List<University> findDistinctByNameAndDepartmentStartsWith(@NonNull String name, @NonNull String department, Pageable pageable);
}
