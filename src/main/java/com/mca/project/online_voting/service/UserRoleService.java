package com.mca.project.online_voting.service;

import com.mca.project.online_voting.entity.UserRole;
import com.mca.project.online_voting.entity.UserRoleId;
import com.mca.project.online_voting.repository.CompanyRepository;
import com.mca.project.online_voting.repository.UserRepository;
import com.mca.project.online_voting.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository; // To check if User exists
    private final CompanyRepository companyRepository; // To check if Company exists

    @Autowired
    public UserRoleService(UserRoleRepository userRoleRepository,
                           UserRepository userRepository,
                           CompanyRepository companyRepository) {
        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    @Transactional
    public UserRole assignUserRole(UserRole userRole) {
        // Validate that the user and company actually exist
        userRepository.findById(userRole.getId().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userRole.getId().getUserId()));
        companyRepository.findById(userRole.getId().getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + userRole.getId().getCompanyId()));

        // Check if the role already exists for this user and company
        userRoleRepository.findById(userRole.getId()).ifPresent(existingRole -> {
            throw new IllegalArgumentException("User already has a role assigned for this company.");
        });

        return userRoleRepository.save(userRole);
    }

    @Transactional(readOnly = true)
    public Optional<UserRole> getUserRole(Long userId, Long companyId) {
        UserRoleId id = new UserRoleId(userId, companyId);
        return userRoleRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<UserRole> getRolesByUserId(Long userId) {
        return userRoleRepository.findByIdUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<UserRole> getRolesByCompanyId(Long companyId) {
        return userRoleRepository.findByIdCompanyId(companyId);
    }

    @Transactional
    public UserRole updateUserRole(Long userId, Long companyId, String newRole) {
        UserRoleId id = new UserRoleId(userId, companyId);
        return userRoleRepository.findById(id).map(userRole -> {
            userRole.setRole(newRole);
            return userRoleRepository.save(userRole);
        }).orElseThrow(() -> new RuntimeException("UserRole not found for userId " + userId + " and companyId " + companyId));
    }

    @Transactional
    public void deleteUserRole(Long userId, Long companyId) {
        UserRoleId id = new UserRoleId(userId, companyId);
        if (!userRoleRepository.existsById(id)) {
            throw new RuntimeException("UserRole not found for userId " + userId + " and companyId " + companyId);
        }
        userRoleRepository.deleteById(id);
    }

    public List<UserRole> findByUserId(Long id) {
        return userRoleRepository.findByIdUserId(id);
    }
}
