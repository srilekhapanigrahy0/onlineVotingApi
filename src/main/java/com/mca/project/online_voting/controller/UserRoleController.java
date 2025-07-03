package com.mca.project.online_voting.controller;

import com.mca.project.online_voting.entity.UserRole;
import com.mca.project.online_voting.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user-roles")
public class UserRoleController {

    private final UserRoleService userRoleService;

    @Autowired
    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @PostMapping
    public ResponseEntity<UserRole> assignRole(@RequestBody UserRole userRole) {
        try {
            UserRole assignedRole = userRoleService.assignUserRole(userRole);
            return new ResponseEntity<>(assignedRole, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Handle cases where user/company not found or role already exists
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{userId}/{companyId}")
    public ResponseEntity<UserRole> getUserRole(@PathVariable Long userId, @PathVariable Long companyId) {
        return userRoleService.getUserRole(userId, companyId)
                .map(userRole -> new ResponseEntity<>(userRole, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserRole>> getRolesByUserId(@PathVariable Long userId) {
        List<UserRole> userRoles = userRoleService.getRolesByUserId(userId);
        return new ResponseEntity<>(userRoles, HttpStatus.OK);
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<UserRole>> getRolesByCompanyId(@PathVariable Long companyId) {
        List<UserRole> userRoles = userRoleService.getRolesByCompanyId(companyId);
        return new ResponseEntity<>(userRoles, HttpStatus.OK);
    }

    @PutMapping("/{userId}/{companyId}")
    public ResponseEntity<UserRole> updateRole(@PathVariable Long userId, @PathVariable Long companyId, @RequestBody String newRole) {
        try {
            UserRole updatedRole = userRoleService.updateUserRole(userId, companyId, newRole);
            return new ResponseEntity<>(updatedRole, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{userId}/{companyId}")
    public ResponseEntity<HttpStatus> deleteRole(@PathVariable Long userId, @PathVariable Long companyId) {
        try {
            userRoleService.deleteUserRole(userId, companyId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
