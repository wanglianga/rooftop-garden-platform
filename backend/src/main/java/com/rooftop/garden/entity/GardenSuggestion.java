package com.rooftop.garden.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "garden_suggestions")
public class GardenSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long gardenerId;

    private Long plotId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SuggestionType type;

    private String title;

    @Column(nullable = false)
    private String content;

    private String suggestionTarget;

    private Boolean handled;

    private String feedback;

    private Long feedbackBy;

    private LocalDateTime feedbackTime;

    private String remark;

    private LocalDateTime createTime;

    public enum SuggestionType {
        PLANTING_ADVICE,
        PEST_CONTROL,
        FERTILIZATION,
        WATERING,
        SOIL_IMPROVEMENT,
        MAINTENANCE,
        OTHER
    }

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        if (handled == null) {
            handled = false;
        }
    }
}
