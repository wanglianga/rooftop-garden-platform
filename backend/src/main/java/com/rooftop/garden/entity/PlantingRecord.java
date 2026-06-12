package com.rooftop.garden.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "planting_records")
public class PlantingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long plotId;

    @Column(nullable = false)
    private Long claimId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String cropName;

    private String cropVariety;

    private String seedSource;

    private LocalDate sowingDate;

    private LocalDate transplantDate;

    private LocalDate expectedHarvestDate;

    private Integer plantCount;

    private String plantingMethod;

    private String remark;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PlantingStatus status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public enum PlantingStatus {
        PLANNED,
        SOWN,
        GERMINATED,
        GROWING,
        FLOWERING,
        FRUITING,
        HARVESTING,
        COMPLETED,
        FAILED
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
