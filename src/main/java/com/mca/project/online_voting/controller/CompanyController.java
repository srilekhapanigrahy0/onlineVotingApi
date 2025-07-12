package com.mca.project.online_voting.controller;
import com.mca.project.online_voting.dto.CompanyDTO;
import com.mca.project.online_voting.entity.Company;
import com.mca.project.online_voting.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/companies") // Base path for all company-related endpoints
public class CompanyController {

    private final CompanyService companyService;

    @Autowired
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("getAllCompanyByUserId/{userId}")
    public ResponseEntity<List<CompanyDTO>> getAllCompanyByUserId(@PathVariable(required = true) int userId) {
        List<CompanyDTO> companies  = companyService.getAllCompanyByUserId(userId);
        return new ResponseEntity<>(companies, HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("createCompany")
    public ResponseEntity<Company> createCompany(@RequestBody Company company) {
        try {
            Company createdCompany = companyService.createCompany(company);
            return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT); // Name already exists
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("getCompanyDetailsById/{company_id}")
    public ResponseEntity<Company> getCompanyDetailsById(@PathVariable Long company_id) {
        return companyService.getCompanyById(company_id)
                .map(company -> new ResponseEntity<>(company, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PutMapping("updateCompanyDetails/{company_id}")
    public ResponseEntity<Company> updateCompanyDetails(@PathVariable Long company_id, @RequestBody Company companyDetails) {
        try {
            Company updatedCompany = companyService.updateCompany(company_id, companyDetails);
            return new ResponseEntity<>(updatedCompany, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Company not found
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @DeleteMapping("deleteCompany/{id}")
    public ResponseEntity<HttpStatus> deleteCompany(@PathVariable Long id) {
        try {
            companyService.deleteCompany(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }













    /*@GetMapping("/name/{name}")
    public ResponseEntity<Company> getCompanyByName(@PathVariable String name) {
        return companyService.getCompanyByName(name)
                .map(company -> new ResponseEntity<>(company, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }*/
}
