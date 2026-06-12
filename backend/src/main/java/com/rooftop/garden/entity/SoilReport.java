package com.rooftop.garden.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "soil_reports")
public class SoilReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long plotId;

    @Column(nullable = false)
    private Long gardenerId;

    private Double phValue;

    private Double moisture;

    private Double nitrogen;

    private Double phosphorus;

    private Double potassium;

    private String organicMatter;

    private String soilTexture;

    private String recommendation;

    private String remark;

    private LocalDateTime testTime;

    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        if (testTime == null) {
            testTime = LocalDateTime.now();
        }
    }
}
