package com.mca.project.online_voting.controller;
import com.mca.project.online_voting.dto.ElectionGroupDTO;
import com.mca.project.online_voting.entity.ElectionGroup;
import com.mca.project.online_voting.service.ElectionGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/electionGroup")
public class ElectionGroupController {
    private final ElectionGroupService electionGroupService;

    @Autowired
    public ElectionGroupController(ElectionGroupService electionGroupService) {
        this.electionGroupService = electionGroupService;
    }

    @GetMapping("getAllElectionGroupsByElectionId/{electionId}")
    public ResponseEntity<List<ElectionGroupDTO>> getAllElectionGroupsByElectionId(@PathVariable(required = true) int electionId) {
        List<ElectionGroupDTO> companies  = electionGroupService.getAllElectionGroupsByElectionId(electionId);
        return new ResponseEntity<>(companies, HttpStatus.OK);
    }

    @GetMapping("getElectionGroupDetailsById/{id}")
    public ResponseEntity<ElectionGroup> getElectionGroupDetailsById(@PathVariable(required = true) Long id) {
        return electionGroupService.getElectionGroupDetailsById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("createElectionGroup")
    public ResponseEntity<ElectionGroup> createElectionGroup(@RequestBody ElectionGroup electionGroup) {
        try {
            ElectionGroup createdElection = electionGroupService.createElectionGroup(electionGroup);
            return new ResponseEntity<>(createdElection, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT); // Name already exists
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("updateElectionGroupDetails/{election_group_id}")
    public ResponseEntity<ElectionGroup> updateElectionGroupDetails(@PathVariable Long election_group_id, @RequestBody ElectionGroup electionGroup) {
        try {
            ElectionGroup updatedCompany = electionGroupService.updateElectionGroupDetails(election_group_id, electionGroup);
            return new ResponseEntity<>(updatedCompany, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Company not found
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("deleteElectionGroup/{id}")
    public ResponseEntity<Void> deleteElectionGroup(@PathVariable Long id) {
        try {
            electionGroupService.deleteElectionGroup(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
