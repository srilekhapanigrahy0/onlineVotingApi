package com.mca.project.online_voting.controller;
import com.mca.project.online_voting.dto.ElectionDTO;
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

    @GetMapping("getElectionById/{id}")
    public ResponseEntity<Election> getElectionById(@PathVariable(required = true) Long id) {
        return electionService.getElectionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }











    @PostMapping
    public Election createElection(@RequestBody Election election) {
        return electionService.createElection(election);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Election> updateElection(@PathVariable Long id, @RequestBody Election election) {
        try {
            return ResponseEntity.ok(electionService.updateElection(id, election));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteElection(@PathVariable Long id) {
        electionService.deleteElection(id);
        return ResponseEntity.noContent().build();
    }
}