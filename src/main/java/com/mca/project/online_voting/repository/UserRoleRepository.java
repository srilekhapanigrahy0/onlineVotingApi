package com.mca.project.online_voting.repository;

import com.mca.project.online_voting.entity.UserRole;
import com.mca.project.online_voting.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {

    // Custom query to find all roles for a specific user
    List<UserRole> findByIdUserId(int userId);

    // Custom query to find all roles for a specific user
    List<UserRole> findByIdUserId(Long userId);

    // Custom query to find all roles for a specific company
    List<UserRole> findByIdCompanyId(Long companyId);

    // Custom query to find a specific role by user and company
    Optional<UserRole> findByIdUserIdAndIdCompanyId(Long userId, Long companyId);
}