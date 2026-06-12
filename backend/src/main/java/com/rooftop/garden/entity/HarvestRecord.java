package com.rooftop.garden.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "harvest_records")
public class HarvestRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long plotId;

    @Column(nullable = false)
    private Long plantingId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String cropName;

    @Column(nullable = false)
    private BigDecimal weight;

    private String unit;

    private String quality;

    private String harvestMethod;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DistributionType distributionType;

    private String recipient;

    private String remark;

    private LocalDateTime harvestTime;

    private LocalDateTime createTime;

    public enum DistributionType {
        SELF_USE,
        DONATION,
        SHARED,
        SOLD
    }

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        if (harvestTime == null) {
            harvestTime = LocalDateTime.now();
        }
    }
}
