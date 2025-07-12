package com.mca.project.online_voting.entity;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "companies") // Table name
@Data // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all fields
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String logo; // URL or path to the company logo

    @Column(name = "created_by")
    private String createdBy; // User ID or name of the creator

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private String status; // e.g., "PENDING", "ACTIVE", "INACTIVE", "REJECTED"

    @Column(name = "approved_by")
    private String approvedBy; // User ID or name of the approver

    @Column(name = "approved_date")
    private LocalDateTime approvedDate;

    private String comment; // Any comments related to approval/status change

    @Column(name = "last_update_date")
    private LocalDateTime lastUpdateDate;
}
