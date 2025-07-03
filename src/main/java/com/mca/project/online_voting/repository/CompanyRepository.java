package com.mca.project.online_voting.repository;

import com.mca.project.online_voting.dto.CompanyDTO;
import com.mca.project.online_voting.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query(nativeQuery = true, value = "SELECT c.id, c.name FROM companies c where id in (SELECT company_id FROM user_roles where user_id = :userId)")
    Optional<List<CompanyDTO>> getAllCompanyByUserId(@Param("userId") int userId);


    // Custom query to find a company by its name (assuming names are unique)
    Optional<Company> findByName(String name);

    // Custom query to find companies by status
    List<Company> findByStatus(String status);
}
