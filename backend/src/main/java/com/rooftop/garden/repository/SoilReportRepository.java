package com.rooftop.garden.repository;

import com.rooftop.garden.entity.SoilReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SoilReportRepository extends JpaRepository<SoilReport, Long> {

    List<SoilReport> findByPlotIdOrderByTestTimeDesc(Long plotId);

    List<SoilReport> findByGardenerIdOrderByTestTimeDesc(Long gardenerId);
}
