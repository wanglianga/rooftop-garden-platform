package com.rooftop.garden.repository;

import com.rooftop.garden.entity.CompostBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompostBatchRepository extends JpaRepository<CompostBatch, Long> {

    Optional<CompostBatch> findByBatchCode(String batchCode);

    List<CompostBatch> findByStatus(CompostBatch.BatchStatus status);

    List<CompostBatch> findByStatusIn(List<CompostBatch.BatchStatus> statuses);

    List<CompostBatch> findByUsedPlotId(Long usedPlotId);

    List<CompostBatch> findBySourceBinId(Long sourceBinId);
}
