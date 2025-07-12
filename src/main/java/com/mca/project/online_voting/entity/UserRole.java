package com.mca.project.online_voting.entity;

import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_roles") // Assuming your table name is user_roles
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRole {

    @EmbeddedId // Indicates a composite primary key
    private UserRoleId id;

    // Many-to-one relationship with User entity
    @ManyToOne(fetch = FetchType.LAZY) // Lazy loading for performance
    @MapsId("userId") // Maps the userId part of the composite key to this relationship
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false) // user_id column is managed by EmbeddedId
    private Users user;

    // Many-to-one relationship with Company entity
    // Assuming you have a Company entity; if not, you'll need to create one
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("companyId") // Maps the companyId part of the composite key to this relationship
    @JoinColumn(name = "company_id", referencedColumnName = "id", insertable = false, updatable = false) // company_id column is managed by EmbeddedId
    private Company company;

    @Column(nullable = false)
    private String role; // e.g., "ADMIN", "USER", "EDITOR"

    // Constructor to easily create UserRole with IDs and role
    public UserRole(Long userId, Long companyId, String role, Company company, Users user) {
        this.id = new UserRoleId(userId, companyId);
        this.role = role;
        this.company = company;
        this.user = user;
    }
}