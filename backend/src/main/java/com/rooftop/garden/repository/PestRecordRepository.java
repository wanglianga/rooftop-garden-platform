package com.rooftop.garden.repository;

import com.rooftop.garden.entity.PestRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PestRecordRepository extends JpaRepository<PestRecord, Long> {

    List<PestRecord> findByPlotIdOrderByDiscoveryTimeDesc(Long plotId);

    List<PestRecord> findByPlantingIdOrderByDiscoveryTimeDesc(Long plantingId);

    List<PestRecord> findByGardenerIdOrderByDiscoveryTimeDesc(Long gardenerId);
}
