package com.mca.project.online_voting.service;
import com.mca.project.online_voting.dto.ElectionGroupDTO;
import com.mca.project.online_voting.entity.ElectionGroup;
import com.mca.project.online_voting.repository.ElectionGroupRepository;
import com.mca.project.online_voting.repository.ElectionRepository;
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

}