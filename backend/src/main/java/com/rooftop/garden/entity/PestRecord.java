package com.rooftop.garden.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "pest_records")
public class PestRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long plotId;

    @Column(nullable = false)
    private Long plantingId;

    @Column(nullable = false)
    private Long gardenerId;

    private String pestType;

    private String pestName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SeverityLevel severity;

    private String description;

    private String treatmentMethod;

    private String treatmentAgent;

    private String effect;

    private String remark;

    private LocalDateTime discoveryTime;

    private LocalDateTime createTime;

    public enum SeverityLevel {
        LOW,
        MEDIUM,
        HIGH,
        SEVERE
    }

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        if (discoveryTime == null) {
            discoveryTime = LocalDateTime.now();
        }
    }
}
