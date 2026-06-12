package com.rooftop.garden.repository;

import com.rooftop.garden.entity.PlantingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlantingRecordRepository extends JpaRepository<PlantingRecord, Long> {

    List<PlantingRecord> findByPlotId(Long plotId);

    List<PlantingRecord> findByUserId(Long userId);

    List<PlantingRecord> findByPlotIdAndStatus(Long plotId, PlantingRecord.PlantingStatus status);

    List<PlantingRecord> findByStatus(PlantingRecord.PlantingStatus status);
}
