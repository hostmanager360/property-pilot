package com.propertypilot.coreservice.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(
        name = "user_apartment_access",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "apartment_id"})
        }
)
public class UserApartmentAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartment_id", nullable = false)
    private Appartamento apartment;

    @Column(name = "created_at", updatable = false, insertable = false)
    private java.time.LocalDateTime createdAt;

}
