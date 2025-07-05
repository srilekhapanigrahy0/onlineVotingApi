package com.mca.project.online_voting.service;
import com.mca.project.online_voting.dto.ElectionDTO;
import com.mca.project.online_voting.entity.Election;
import com.mca.project.online_voting.repository.ElectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;






@Service
public class ElectionService {

    @Autowired
    private ElectionRepository electionRepository;

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
        return electionRepository.save(election);
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