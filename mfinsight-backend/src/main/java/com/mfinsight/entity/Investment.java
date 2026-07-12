package com.mfinsight.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "investments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mutual_fund_id", nullable = false)
    private MutualFund mutualFund;

    @Column(nullable = false)
    private Double investedAmount;

    @Column(nullable = false)
    private Double units;

    @Column(nullable = false)
    private Double navAtPurchase;

    @Column(nullable = false)
    private LocalDate purchaseDate;

    @Builder.Default
    private boolean isSip = false;
    private Double sipAmount;
    private String sipFrequency;

    @Column(nullable = false)
    @Builder.Default
    private String status = "ACTIVE";

    private LocalDate redemptionDate;
    private Double redemptionNav;
    private Double redemptionAmount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}