package com.rooftop.garden.repository;

import com.rooftop.garden.entity.SeedlingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeedlingRecordRepository extends JpaRepository<SeedlingRecord, Long> {

    List<SeedlingRecord> findByPlotIdOrderByReplantTimeDesc(Long plotId);

    List<SeedlingRecord> findByPlantingIdOrderByReplantTimeDesc(Long plantingId);

    List<SeedlingRecord> findByGardenerIdOrderByReplantTimeDesc(Long gardenerId);
}
