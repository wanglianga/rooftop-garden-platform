package com.rooftop.garden.repository;

import com.rooftop.garden.entity.CareLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareLogRepository extends JpaRepository<CareLog, Long> {

    List<CareLog> findByPlotIdOrderByCareTimeDesc(Long plotId);

    List<CareLog> findByPlantingIdOrderByCareTimeDesc(Long plantingId);

    List<CareLog> findByUserIdOrderByCareTimeDesc(Long userId);

    List<CareLog> findByPlotIdAndCareType(Long plotId, CareLog.CareType careType);
}
