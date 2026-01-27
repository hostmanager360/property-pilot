package com.propertypilot.coreservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "first_access_steps")
@Getter @Setter
public class FirstAccessStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String code; // CREATE_TENANT, COMPLETE_USER_DETAIL, DASHBOARD

    private String description;
}
