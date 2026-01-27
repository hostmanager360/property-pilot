package com.propertypilot.authservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean enabled = false;

    @Column(name = "tenant_key", length = 50)
    private String tenantKey;

    // 🔥 Nuovo: obbligo reset password
    @Column(name = "password_reset_required")
    private Boolean passwordResetRequired = false;

    // 🔥 Nuovo: primo accesso dopo reset
    @Column(name = "first_access_completed")
    private Boolean firstAccessCompleted = false;

    // 🔥 Nuovo: token reset password
    @Column(name = "reset_password_token", length = 255)
    private String resetPasswordToken;

    // 🔥 Nuovo: scadenza token reset
    @Column(name = "reset_password_expires_at")
    private LocalDateTime resetPasswordExpiresAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role roleEntity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "first_access_step_id", nullable = false)
    private FirstAccessStep firstAccessStep;


    // 🔥 Auto-set timestamps
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}