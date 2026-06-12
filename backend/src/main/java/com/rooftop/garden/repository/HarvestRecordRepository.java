package com.rooftop.garden.repository;

import com.rooftop.garden.entity.HarvestRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HarvestRecordRepository extends JpaRepository<HarvestRecord, Long> {

    List<HarvestRecord> findByPlotIdOrderByHarvestTimeDesc(Long plotId);

    List<HarvestRecord> findByUserIdOrderByHarvestTimeDesc(Long userId);

    List<HarvestRecord> findByPlantingIdOrderByHarvestTimeDesc(Long plantingId);

    List<HarvestRecord> findByDistributionType(HarvestRecord.DistributionType distributionType);
}
