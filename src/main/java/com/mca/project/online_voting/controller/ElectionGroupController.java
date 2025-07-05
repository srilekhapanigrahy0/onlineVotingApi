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


@CrossOrigin(origins = "http://localhost:3000")
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
}
