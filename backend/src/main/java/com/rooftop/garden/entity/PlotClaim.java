package com.rooftop.garden.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "plot_claims")
public class PlotClaim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long plotId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ClaimStatus status;

    private LocalDate claimDate;

    private LocalDate startDate;

    private LocalDate endDate;

    private String cropType;

    private String wateringSchedule;

    private Boolean willingToDonateCompost;

    private String harvestPreference;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public enum ClaimStatus {
        PENDING,
        APPROVED,
        ACTIVE,
        EXPIRED,
        CANCELLED
    }

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
