package com.mca.project.online_voting.service;
import com.mca.project.online_voting.dto.ElectionDTO;
import com.mca.project.online_voting.entity.Election;
import com.mca.project.online_voting.entity.ElectionGroup;
import com.mca.project.online_voting.repository.ElectionGroupRepository;
import com.mca.project.online_voting.repository.ElectionRepository;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;






@Service
public class ElectionService {

    private final ElectionRepository electionRepository;
    private final ElectionGroupRepository electionGroupRepository;

    @Autowired
    public ElectionService(ElectionRepository electionRepository, ElectionGroupRepository electionGroupRepository) {
        this.electionRepository = electionRepository;
        this.electionGroupRepository = electionGroupRepository;
    }

    public List<ElectionDTO> getAllElectionsByUserId(int userId) {
        Optional<List<ElectionDTO>> electionDetails = electionRepository.getAllElectionsByUserId(userId);
        if(electionDetails.isEmpty()) {
            return null;
        } else
            return  electionDetails.get();
    }

    public List<ElectionDTO> getAllElectionsByCompanyId(int companyId) {
        Optional<List<ElectionDTO>> electionDetails = electionRepository.getAllElectionsByCompanyId(companyId);
        if(electionDetails.isEmpty()) {
            return null;
        } else
            return  electionDetails.get();
    }

    public Optional<Election> getElectionById(Long id) {
        return electionRepository.findById(id);
    }

    public Election createElection(Election election) {
        Election e = electionRepository.save(election);

        ElectionGroup eg = new  ElectionGroup();
        eg.setElectionId(election.getId());
        eg.setName(election.getName());
        eg.setStartDate(election.getStartDate());
        eg.setEndDate(election.getEndDate());
        eg.setDetails(election.getDetails());
        eg.setCreatedBy(election.getCreatedBy());
        eg.setCreatedDate(election.getCreatedDate());
        eg.setStatus(election.getStatus());
        eg.setApprovedBy(election.getApprovedBy());
        eg.setApprovedDate(election.getApprovedDate());
        eg.setComment(election.getComment());
        eg.setUpdatedBy(election.getCreatedBy());
        eg.setLastUpdateDate(election.getLastUpdateDate());
        electionGroupRepository.save(eg);

        return  e;
    }

    public Election updateElection(Long id, Election updatedElection) {
        return electionRepository.findById(id)
                .map(existing -> {
                    existing.setCompanyId(updatedElection.getCompanyId());
                    existing.setName(updatedElection.getName());
                    existing.setStartDate(updatedElection.getStartDate());
                    existing.setEndDate(updatedElection.getEndDate());
                    existing.setDetails(updatedElection.getDetails());
                    existing.setCreatedBy(updatedElection.getCreatedBy());
                    existing.setCreatedDate(updatedElection.getCreatedDate());
                    existing.setStatus(updatedElection.getStatus());
                    existing.setApprovedBy(updatedElection.getApprovedBy());
                    existing.setApprovedDate(updatedElection.getApprovedDate());
                    existing.setComment(updatedElection.getComment());
                    existing.setLastUpdateDate(updatedElection.getLastUpdateDate());
                    return electionRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Election not found with id " + id));
    }

    public void deleteElection(Long id) {
        electionRepository.deleteById(id);
    }
}