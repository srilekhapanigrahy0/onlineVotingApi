package com.mca.project.online_voting.repository;

import com.mca.project.online_voting.dto.ElectionDTO;
import com.mca.project.online_voting.entity.Election;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface ElectionRepository extends JpaRepository<Election, Long> {

    @Query(nativeQuery = true, value = "SELECT e.id, e.company_id, e.name, e.start_date, e.end_date, e.created_by, e.status FROM election e where company_id in (SELECT c.id FROM companies c where id in (SELECT company_id FROM user_roles where user_id = :userId))")
    Optional<List<ElectionDTO>> getAllElectionsByUserId(@Param("userId") int userId);

    @Query(nativeQuery = true, value = "SELECT e.id, e.company_id, e.name, e.start_date, e.end_date, e.created_by, e.status FROM election e where company_id in (:companyId)")
    Optional<List<ElectionDTO>> getAllElectionsByCompanyId(@Param("companyId") int companyId);

}