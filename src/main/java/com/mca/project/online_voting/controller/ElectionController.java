package com.mca.project.online_voting.controller;
import com.mca.project.online_voting.dto.ElectionDTO;
import com.mca.project.online_voting.entity.Company;
import com.mca.project.online_voting.entity.Election;
import com.mca.project.online_voting.service.ElectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/elections")
public class ElectionController {

    private final ElectionService electionService;

    @Autowired
    public ElectionController(ElectionService electionService) {
        this.electionService = electionService;
    }

    @GetMapping("getAllElectionsByUserId/{userId}")
    public ResponseEntity<List<ElectionDTO>> getAllElectionsByUserId(@PathVariable(required = true) int userId) {
        List<ElectionDTO> companies  = electionService.getAllElectionsByUserId(userId);
        return new ResponseEntity<>(companies, HttpStatus.OK);
    }

    @GetMapping("getAllElectionsByCompanyId/{companyId}")
    public ResponseEntity<List<ElectionDTO>> getAllElectionsByCompanyId(@PathVariable(required = true) int companyId) {
        List<ElectionDTO> companies  = electionService.getAllElectionsByCompanyId(companyId);
        return new ResponseEntity<>(companies, HttpStatus.OK);
    }

    @GetMapping("getElectionDetailsById/{id}")
    public ResponseEntity<Election> getElectionById(@PathVariable(required = true) Long id) {
        return electionService.getElectionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("createElection")
    public ResponseEntity<Election> createElection(@RequestBody Election election) {
        try {
            Election createdElection = electionService.createElection(election);
            return new ResponseEntity<>(createdElection, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT); // Name already exists
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("updateElectionDetails/{election_id}")
    public ResponseEntity<Election> updateElectionDetails(@PathVariable Long election_id, @RequestBody Election election) {
        try {
            Election updatedCompany = electionService.updateElection(election_id, election);
            return new ResponseEntity<>(updatedCompany, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Company not found
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("deleteElection/{id}")
    public ResponseEntity<Void> deleteElection(@PathVariable Long id) {
        try {
            electionService.deleteElection(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}