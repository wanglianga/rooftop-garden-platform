package com.rooftop.garden.repository;

import com.rooftop.garden.entity.Plot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlotRepository extends JpaRepository<Plot, Long> {

    Optional<Plot> findByPlotCode(String plotCode);

    List<Plot> findByStatus(Plot.PlotStatus status);

    List<Plot> findByStatusIn(List<Plot.PlotStatus> statuses);
}
