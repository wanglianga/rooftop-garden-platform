package com.rooftop.garden.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "compost_bins")
public class CompostBin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String binCode;

    private String location;

    @Column(nullable = false)
    private BigDecimal capacity;

    private BigDecimal currentWeight;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BinStatus status;

    private String binType;

    private String remark;

    private LocalDateTime lastMaintenanceDate;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public enum BinStatus {
        ACTIVE,
        FILLING,
        FULL,
        FERMENTING,
        MAINTENANCE,
        INACTIVE
    }

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (currentWeight == null) {
            currentWeight = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
