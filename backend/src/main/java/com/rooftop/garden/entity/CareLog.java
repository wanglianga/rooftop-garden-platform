package com.rooftop.garden.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "care_logs")
public class CareLog {

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
    @Enumerated(EnumType.STRING)
    private CareType careType;

    private String description;

    private LocalDateTime careTime;

    private String weather;

    private Double temperature;

    private String remark;

    public enum CareType {
        WATERING,
        FERTILIZING,
        PEST_CONTROL,
        PRUNING,
        WEEDING,
        SOIL_TEST,
        OTHER
    }

    @PrePersist
    protected void onCreate() {
        if (careTime == null) {
            careTime = LocalDateTime.now();
        }
    }
}
