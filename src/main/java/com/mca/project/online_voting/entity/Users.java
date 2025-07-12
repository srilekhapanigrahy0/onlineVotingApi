package com.mca.project.online_voting.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users") // Good practice to pluralize table names, or match your liquibase script
@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all fields
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true) // Assuming phone numbers should be unique
    private String phone;

    @Column(nullable = false, unique = true) // Email should be unique and not null
    private String email;

    @Column(name = "mail_verification") // Map to your column name if it differs from camelCase
    private Boolean mailVerification; // Using Boolean for clarity, can be boolean if never null

    @Column(name = "userid", unique = true, nullable = false) // Assuming userid is a unique identifier
    private String userId;

    @Column(nullable = false)
    private String password;

    private String gender;

    private String photo;

    @Column(name = "regd_date")
    private LocalDateTime regdDate; // Use LocalDateTime for date and time

    @Transient // Not persisted in DB, will be populated at runtime
    public Set<String> getUserRoles() {
        // This will now need to be explicitly set or fetched when the User object is loaded
        return java.util.Collections.emptySet(); // Default empty
    }






    // You can add custom methods here if needed, but Lombok handles basic accessors.
}
