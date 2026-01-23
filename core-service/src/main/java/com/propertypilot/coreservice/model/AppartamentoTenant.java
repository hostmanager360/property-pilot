package com.propertypilot.coreservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "appartamento_tenant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppartamentoTenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appartamento_id", nullable = false)
    private Appartamento appartamento;

    @Column(name = "tenant_key", length = 50)
    private String tenantKey;
}
