package com.campus.entity;

import com.campus.enums.ApplicationStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "job_applications")
@Data
@NoArgsConstructor
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;

    private Long jobPostingId;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status; // Example: APPLIED, APPROVED, REJECTED

}
