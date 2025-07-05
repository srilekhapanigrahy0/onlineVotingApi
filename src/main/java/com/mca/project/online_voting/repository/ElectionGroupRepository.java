package com.mca.project.online_voting.repository;
import com.mca.project.online_voting.dto.ElectionGroupDTO;
import com.mca.project.online_voting.entity.ElectionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface ElectionGroupRepository extends JpaRepository<ElectionGroup, Long> {

    @Query(nativeQuery = true, value = "SELECT e.id, e.election_id, e.name, e.start_date, e.end_date, e.created_by, e.status FROM election_group e where election_id in (:electionId)")
    Optional<List<ElectionGroupDTO>> getAllElectionGroupsByElectionId(@Param("electionId") int electionId);

}