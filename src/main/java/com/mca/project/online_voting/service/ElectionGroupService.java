package com.mca.project.online_voting.service;
import com.mca.project.online_voting.dto.ElectionGroupDTO;
import com.mca.project.online_voting.entity.ElectionGroup;
import com.mca.project.online_voting.repository.ElectionGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class ElectionGroupService {

    private final ElectionGroupRepository electionGroupRepository;

    @Autowired
    public ElectionGroupService(ElectionGroupRepository electionGroupRepository) {
        this.electionGroupRepository = electionGroupRepository;
    }

    public List<ElectionGroupDTO> getAllElectionGroupsByElectionId(int electionId) {
        Optional<List<ElectionGroupDTO>> electionGroupDetails = electionGroupRepository.getAllElectionGroupsByElectionId(electionId);
        if(electionGroupDetails.isEmpty()) {
            return null;
        } else
            return  electionGroupDetails.get();
    }

    public Optional<ElectionGroup> getElectionGroupDetailsById(Long id) {
        return electionGroupRepository.findById(id);
    }

    @Transactional
    public ElectionGroup createElectionGroup(ElectionGroup electionGroup) {
        return electionGroupRepository.save(electionGroup);
    }

    @Transactional
    public ElectionGroup updateElectionGroupDetails(Long id, ElectionGroup updatedElectionGroup) {
        return electionGroupRepository.findById(id)
                .map(existing -> {
                    existing.setName(updatedElectionGroup.getName());
                    existing.setStartDate(updatedElectionGroup.getStartDate());
                    existing.setEndDate(updatedElectionGroup.getEndDate());
                    existing.setDetails(updatedElectionGroup.getDetails());
                    existing.setCreatedBy(updatedElectionGroup.getCreatedBy());
                    existing.setCreatedDate(updatedElectionGroup.getCreatedDate());
                    existing.setStatus(updatedElectionGroup.getStatus());
                    existing.setApprovedBy(updatedElectionGroup.getApprovedBy());
                    existing.setApprovedDate(updatedElectionGroup.getApprovedDate());
                    existing.setComment(updatedElectionGroup.getComment());
                    existing.setUpdatedBy(updatedElectionGroup.getUpdatedBy());
                    existing.setLastUpdateDate(updatedElectionGroup.getLastUpdateDate());
                    return electionGroupRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Election Group not found with id " + id));
    }

    @Transactional
    public boolean deleteElectionGroup(long id) {
        if (electionGroupRepository.existsById(id)) {
            electionGroupRepository.deleteById(id);
            return true;
        }
        return false;
    }

}