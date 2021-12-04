package de.fernunihagen.techsearch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import de.fernunihagen.techsearch.data.ApplicationJobEnum;
import de.fernunihagen.techsearch.data.ApplicationJobState;

@Repository
public interface ApplicationJobStateRepository extends JpaRepository<ApplicationJobState, Integer> {

    @Query("select s from ApplicationJobState s where s.jobType = ?1")
    ApplicationJobState findApplicationJobState(ApplicationJobEnum applicationJobEnum);
}
