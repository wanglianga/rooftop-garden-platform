package com.rooftop.garden.service;

import com.rooftop.garden.entity.CompostBatch;
import com.rooftop.garden.entity.Plot;
import com.rooftop.garden.repository.CompostBatchRepository;
import com.rooftop.garden.repository.PlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CompostBatchService {

    @Autowired
    private CompostBatchRepository batchRepository;

    @Autowired
    private PlotRepository plotRepository;

    public List<CompostBatch> getAllBatches() {
        return batchRepository.findAll();
    }

    public Optional<CompostBatch> getBatchById(Long id) {
        return batchRepository.findById(id);
    }

    public Optional<CompostBatch> getBatchByCode(String code) {
        return batchRepository.findByBatchCode(code);
    }

    public List<CompostBatch> getBatchesByStatus(CompostBatch.BatchStatus status) {
        return batchRepository.findByStatus(status);
    }

    public CompostBatch createBatch(CompostBatch batch) {
        if (batch.getStatus() == null) {
            batch.setStatus(CompostBatch.BatchStatus.COLLECTING);
        }
        if (batch.getStartDate() == null) {
            batch.setStartDate(LocalDate.now());
        }
        if (batch.getTurnCount() == null) {
            batch.setTurnCount(0);
        }
        return batchRepository.save(batch);
    }

    public CompostBatch updateBatch(CompostBatch batch) {
        return batchRepository.save(batch);
    }

    public CompostBatch startFermentation(Long batchId) {
        return batchRepository.findById(batchId)
                .map(batch -> {
                    batch.setStatus(CompostBatch.BatchStatus.FERMENTING);
                    return batchRepository.save(batch);
                })
                .orElseThrow(() -> new RuntimeException("Batch not found"));
    }

    public CompostBatch turnCompost(Long batchId) {
        return batchRepository.findById(batchId)
                .map(batch -> {
                    batch.setTurnCount(batch.getTurnCount() + 1);
                    batch.setLastTurnDate(LocalDate.now());
                    return batchRepository.save(batch);
                })
                .orElseThrow(() -> new RuntimeException("Batch not found"));
    }

    public CompostBatch markMature(Long batchId) {
        return batchRepository.findById(batchId)
                .map(batch -> {
                    batch.setStatus(CompostBatch.BatchStatus.MATURE);
                    batch.setMaturityDate(LocalDate.now());
                    return batchRepository.save(batch);
                })
                .orElseThrow(() -> new RuntimeException("Batch not found"));
    }

    @Transactional
    public CompostBatch applyToPlot(Long batchId, Long plotId, String destination) {
        CompostBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found"));
        
        batch.setStatus(CompostBatch.BatchStatus.USED);
        batch.setUsedPlotId(plotId);
        batch.setMatureDestination(destination);
        
        Plot plot = plotRepository.findById(plotId)
                .orElseThrow(() -> new RuntimeException("Plot not found"));

        return batchRepository.save(batch);
    }
}
