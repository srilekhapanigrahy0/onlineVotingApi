package com.mca.project.online_voting.service;
import com.mca.project.online_voting.dto.CompanyDTO;
import com.mca.project.online_voting.entity.Company;
import com.mca.project.online_voting.entity.UserRole;
import com.mca.project.online_voting.repository.CompanyRepository;
import com.mca.project.online_voting.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    @Autowired
    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Transactional(readOnly = true)
    public List<CompanyDTO> getAllCompanyByUserId(int userId) {
        Optional<List<CompanyDTO>> companyDetails = companyRepository.getAllCompanyByUserId(userId);
        if(companyDetails.isEmpty()) {
            return null;
        } else
            return  companyDetails.get();
    }

    @Transactional
    public Company createCompany(Company company) {
        // Set creation date and initial status
        if (company.getCreatedDate() == null) {
            company.setCreatedDate(LocalDateTime.now());
        }
        if (company.getLastUpdateDate() == null) {
            company.setLastUpdateDate(LocalDateTime.now());
        }
        if (company.getStatus() == null || company.getStatus().isEmpty()) {
            company.setStatus("P"); // Default initial status
        }

        // Validate unique name
        companyRepository.findByName(company.getName()).ifPresent(c -> {
            throw new IllegalArgumentException("Company with this name already exists.");
        });

        Company insertedCompany = companyRepository.save(company);


        /*UserRole userRole = new UserRole();
        long companyId = insertedCompany.getId();
        userRole.setRole("Creator");
        userRole.setCompany(insertedCompany);
        userRole.user setUser(getUserById(1));
        userRoleService.assignUserRole(userRole);*/

        return insertedCompany;
    }





















    @Transactional(readOnly = true)
    public Optional<Company> getCompanyById(Long id) {
        return companyRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Company> getCompanyByName(String name) {
        return companyRepository.findByName(name);
    }

    @Transactional(readOnly = true)
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Company> getCompaniesByStatus(String status) {
        return companyRepository.findByStatus(status);
    }

    @Transactional
    public Company updateCompany(Long id, Company companyDetails) {
        return companyRepository.findById(id).map(company -> {
            company.setName(companyDetails.getName());
            company.setLogo(companyDetails.getLogo());
            company.setStatus(companyDetails.getStatus());
            company.setApprovedBy(companyDetails.getApprovedBy());
            company.setApprovedDate(companyDetails.getApprovedDate());
            company.setComment(companyDetails.getComment());
            company.setLastUpdateDate(LocalDateTime.now()); // Update last update date

            // You might want to add logic here for when status changes to "APPROVED"
            // For example, setting approvedBy and approvedDate if not already set
            if ("APPROVED".equalsIgnoreCase(companyDetails.getStatus()) && company.getApprovedDate() == null) {
                company.setApprovedDate(LocalDateTime.now());
                // You might get approvedBy from security context
                company.setApprovedBy(companyDetails.getApprovedBy() != null ? companyDetails.getApprovedBy() : "SYSTEM");
            }

            return companyRepository.save(company);
        }).orElseThrow(() -> new RuntimeException("Company not found with id " + id));
    }

    @Transactional
    public void deleteCompany(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new RuntimeException("Company not found with id " + id);
        }
        companyRepository.deleteById(id);
    }
}
